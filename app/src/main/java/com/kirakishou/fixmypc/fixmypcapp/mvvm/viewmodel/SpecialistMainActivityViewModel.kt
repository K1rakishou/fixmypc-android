package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.HasAlreadyRespondedResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.RespondToDamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ActiveDamageClaimListFragmentErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ActiveDamageClaimListFragmentInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ActiveDamageClaimListFragmentOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class SpecialistMainActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mWifiUtils: WifiUtils,
                    protected val mDamageClaimRepo: DamageClaimRepository,
                    protected val mSchedulers: SchedulerProvider) :
        BaseViewModel(),
        ActiveDamageClaimListFragmentInputs,
        ActiveDamageClaimListFragmentOutputs,
        ActiveDamageClaimListFragmentErrors {

    val mInputs: ActiveDamageClaimListFragmentInputs = this
    val mOutputs: ActiveDamageClaimListFragmentOutputs = this
    val mErrors: ActiveDamageClaimListFragmentErrors = this

    private val itemsPerPage = Constant.MAX_DAMAGE_CLAIMS_PER_PAGE
    private val mCompositeDisposable = CompositeDisposable()

    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnHasAlreadyRespondedResponse: BehaviorSubject<Boolean>
    lateinit var mCheckHasAlreadyRespondedSubject: BehaviorSubject<Long>
    lateinit var mOnRespondToDamageClaimSuccessSubject: BehaviorSubject<Unit>
    lateinit var mRespondToDamageClaimSubject: BehaviorSubject<Long>
    lateinit var mGetDamageClaimsWithinRadiusSubject: BehaviorSubject<GetDamageClaimsRequestParamsDTO>
    lateinit var mOnDamageClaimsPageReceivedSubject: BehaviorSubject<ArrayList<DamageClaimsWithDistanceDTO>>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>
    lateinit var mEitherFromRepoOrServerSubject: BehaviorSubject<Pair<LatLng, DamageClaimsResponse>>

    fun init() {
        Timber.e("SpecialistMainActivityViewModel init()")
        mCompositeDisposable.clear()

        mOnBadResponseSubject = BehaviorSubject.create()
        mOnHasAlreadyRespondedResponse = BehaviorSubject.create()
        mCheckHasAlreadyRespondedSubject = BehaviorSubject.create()
        mOnRespondToDamageClaimSuccessSubject = BehaviorSubject.create()
        mRespondToDamageClaimSubject = BehaviorSubject.create()
        mGetDamageClaimsWithinRadiusSubject = BehaviorSubject.create()
        mOnDamageClaimsPageReceivedSubject = BehaviorSubject.create()
        mOnUnknownErrorSubject = BehaviorSubject.create()
        mEitherFromRepoOrServerSubject = BehaviorSubject.create()

        mCompositeDisposable += mEitherFromRepoOrServerSubject
                .map { (latlon, response) -> calcDistances(latlon.latitude, latlon.longitude, response) }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        mCompositeDisposable += mRespondToDamageClaimSubject
                .flatMap { mApiClient.respondToDamageClaim(RespondToDamageClaimPacket(it)).toObservable() }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        mCompositeDisposable += mCheckHasAlreadyRespondedSubject
                .flatMap { mApiClient.checkAlreadyRespondedToDamageClaim(it).toObservable() }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        val repositoryItemsObservable = mGetDamageClaimsWithinRadiusSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { (latlon, radius, skip) ->
                    val repoResultObservable = mDamageClaimRepo.findWithinBBox(latlon.latitude, latlon.longitude, radius, skip)
                            .map { DamageClaimsResponse(it.toMutableList(), ErrorCode.Remote.REC_OK) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), Observable.just(radius),
                            Observable.just(skip), repoResultObservable, { o1, o2, o3, o4 -> IsRepoEmptyDTO(o1, o2, o3, o4) })
                }
                .publish()
                .autoConnect(2)

        repositoryItemsObservable
                .filter { it.response.damageClaims.size >= itemsPerPage }
                .doOnNext { Timber.e("Retrieving items from repo") }
                .flatMap { Observables.zip(Observable.just(it.latlon), Observable.just(it.response)) }
                .subscribe(mEitherFromRepoOrServerSubject)

        repositoryItemsObservable
                .filter { it.response.damageClaims.size < itemsPerPage }
                .doOnNext { Timber.e("Fetching items from server") }
                .flatMap { (latlon, radius, skip) ->
                    val repoResultObservable = mApiClient.getDamageClaims(latlon.latitude, latlon.longitude, radius, skip, itemsPerPage)
                            .doOnSuccess { serverResponse -> mDamageClaimRepo.saveAll(serverResponse.damageClaims) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), Observable.just(radius),
                            Observable.just(skip), repoResultObservable, { o1, o2, o3, o4 -> IsRepoEmptyDTO(o1, o2, o3, o4) })
                }
                .flatMap { Observables.zip(Observable.just(it.latlon), Observable.just(it.response)) }
                .subscribe(mEitherFromRepoOrServerSubject)
    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("SpecialistMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    override fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long) {
        mGetDamageClaimsWithinRadiusSubject.onNext(GetDamageClaimsRequestParamsDTO(latLng, radius, page * itemsPerPage))
    }

    override fun respondToDamageClaim(damageClaimId: Long) {
        mRespondToDamageClaimSubject.onNext(damageClaimId)
    }

    override fun checkHasAlreadyRespondedToDamageClaim(damageClaimId: Long) {
        mCheckHasAlreadyRespondedSubject.onNext(damageClaimId)
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is RespondToDamageClaimResponse -> {
                    mOnRespondToDamageClaimSuccessSubject.onNext(Unit)
                }

                is DistanceWithDamageClaimResponse -> {
                    mOnDamageClaimsPageReceivedSubject.onNext(response.damageClaims)
                }

                is HasAlreadyRespondedResponse -> {
                    mOnHasAlreadyRespondedResponse.onNext(response.hasAlreadyResponded)
                }
            }
        } else {
            when (response) {
                is RespondToDamageClaimResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_COULD_NOT_RESPOND_TO_DAMAGE_CLAIM,
                        ErrorCode.Remote.REC_DAMAGE_CLAIM_DOES_NOT_EXIST,
                        ErrorCode.Remote.REC_DAMAGE_CLAIM_IS_NOT_ACTIVE,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> throw RuntimeException("Unknown errorCode: $errorCode")
                    }
                }

                is DistanceWithDamageClaimResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> throw RuntimeException("Unknown errorCode: $errorCode")
                    }
                }

                is HasAlreadyRespondedResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> throw RuntimeException("Unknown errorCode: $errorCode")
                    }
                }
            }
        }
    }

    private fun handleError(error: Throwable) {
        mOnUnknownErrorSubject.onNext(error)
    }

    private fun calcDistances(lat: Double, lon: Double, response: DamageClaimsResponse): DistanceWithDamageClaimResponse {
        val retVal = DistanceWithDamageClaimResponse(arrayListOf(), response.errorCode)

        for (damageClaim in response.damageClaims) {
            val dist = MathUtils.distance(lat, lon, damageClaim.lat, damageClaim.lon)
            val dcwd = DamageClaimsWithDistanceDTO(MathUtils.round(dist, 2), damageClaim)
            retVal.damageClaims.add(dcwd)
        }

        retVal.damageClaims.sortWith(DistanceComparator())
        return retVal
    }

    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
    override fun onDamageClaimsPageReceived(): Observable<ArrayList<DamageClaimsWithDistanceDTO>> = mOnDamageClaimsPageReceivedSubject
    override fun onRespondToDamageClaimSuccessSubject(): Observable<Unit> = mOnRespondToDamageClaimSuccessSubject
    override fun onHasAlreadyRespondedResponse(): Observable<Boolean> = mOnHasAlreadyRespondedResponse

    data class IsRepoEmptyDTO(val latlon: LatLng,
                              val radius: Double,
                              val page: Long,
                              val response: DamageClaimsResponse)

    inner class GetDamageClaimsRequestParamsDTO(val latlon: LatLng,
                                                val radius: Double,
                                                val skip: Long) {

        operator fun component1() = latlon
        operator fun component2() = radius
        operator fun component3() = skip
    }

    inner class DistanceWithDamageClaimResponse(var damageClaims: ArrayList<DamageClaimsWithDistanceDTO>,
                                                errorCode: ErrorCode.Remote) : StatusResponse(errorCode)

    inner class DistanceComparator : Comparator<DamageClaimsWithDistanceDTO> {

        override fun compare(p0: DamageClaimsWithDistanceDTO, p1: DamageClaimsWithDistanceDTO): Int {
            if (p0.distance < p1.distance) {
                return -1
            }

            if (p0.distance > p1.distance) {
                return 1
            }

            return 0
        }
    }
}