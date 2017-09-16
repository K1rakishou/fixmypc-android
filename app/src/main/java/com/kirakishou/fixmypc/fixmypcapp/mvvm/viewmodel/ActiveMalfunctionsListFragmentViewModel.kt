package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ActiveMalfunctionsListFragmentErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ActiveMalfunctionsListFragmentInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ActiveMalfunctionsListFragmentOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class ActiveMalfunctionsListFragmentViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mWifiUtils: WifiUtils,
                    protected val mDamageClaimRepo: DamageClaimRepository) : BaseViewModel(),
        ActiveMalfunctionsListFragmentInputs,
        ActiveMalfunctionsListFragmentOutputs,
        ActiveMalfunctionsListFragmentErrors {

    val mInputs: ActiveMalfunctionsListFragmentInputs = this
    val mOutputs: ActiveMalfunctionsListFragmentOutputs = this
    val mErrors: ActiveMalfunctionsListFragmentErrors = this

    private val itemsPerPage = Constant.MAX_DAMAGE_CLAIMS_PER_PAGE
    private val mCompositeDisposable = CompositeDisposable()

    private lateinit var mIsFirstFragmentStartSubject: BehaviorSubject<Boolean>
    private lateinit var mRequestParamsSubject: BehaviorSubject<GetDamageClaimsRequestParamsDTO>
    private lateinit var mOnDamageClaimsPageReceivedSubject: BehaviorSubject<ArrayList<DamageClaimsWithDistanceDTO>>
    private lateinit var mOnNothingFoundSubject: BehaviorSubject<Unit>
    private lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    private lateinit var mSendRequestSubject: BehaviorSubject<GetDamageClaimsRequestParamsDTO>
    private lateinit var mEitherFromRepoOrServerSubject: BehaviorSubject<Pair<LatLng, DamageClaimsResponse>>

    fun init() {
        mCompositeDisposable.clear()

        mIsFirstFragmentStartSubject = BehaviorSubject.create<Boolean>()
        mRequestParamsSubject = BehaviorSubject.create<GetDamageClaimsRequestParamsDTO>()
        mOnDamageClaimsPageReceivedSubject = BehaviorSubject.create<ArrayList<DamageClaimsWithDistanceDTO>>()
        mOnNothingFoundSubject = BehaviorSubject.create<Unit>()
        mOnUnknownErrorSubject = BehaviorSubject.create<Throwable>()

        mSendRequestSubject = BehaviorSubject.create<GetDamageClaimsRequestParamsDTO>()
        mEitherFromRepoOrServerSubject = BehaviorSubject.create<Pair<LatLng, DamageClaimsResponse>>()

        //TODO: write tests for this shit ASAP
        mCompositeDisposable += mSendRequestSubject
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() " +
                        "Fetching damage claims from the server") }
                .flatMap { (latlon, radius, page) ->
                    val responseObservable = mApiClient.getDamageClaims(latlon.latitude,
                            latlon.longitude, radius, page, itemsPerPage)
                            .doOnSuccess { response -> mDamageClaimRepo.saveAll(response.damageClaims) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), responseObservable)
                }
                .map { (latlon, response) -> calcDistances(latlon.latitude, latlon.longitude, response) }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        mCompositeDisposable += mEitherFromRepoOrServerSubject
                .map { (latlon, response) -> calcDistances(latlon.latitude, latlon.longitude, response) }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        //multicast params and rotation state to three different observables
        val locationAndIsFirstStartObservable = Observables.combineLatest(mRequestParamsSubject,
                mIsFirstFragmentStartSubject)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() Start multicasting") }
                .publish()
                .autoConnect(4)

        locationAndIsFirstStartObservable
                .filter { (_, isFirstFragmentStart) -> isFirstFragmentStart }
                .filter { _ -> mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() has wifi " +
                        "and was not rotated. page: ${it.page}") }
                .subscribe(mSendRequestSubject)

        val rotatedHasWifi = locationAndIsFirstStartObservable
                .filter { (_, isFirstFragmentStart) -> !isFirstFragmentStart }
                .filter { _ -> mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() has wifi " +
                        "but device was rotated since fragment start. page: ${it.page}") }

        val notRotatedNoWifi = locationAndIsFirstStartObservable
                .filter { (_, isFirstFragmentStart) -> isFirstFragmentStart }
                .filter { _ -> !mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() device " +
                        "was not rotated after fragment start but no wifi. page: ${it.page}") }

        val rotatedNoWifi = locationAndIsFirstStartObservable
                .filter { (_, isFirstFragmentStart) -> !isFirstFragmentStart }
                .filter { _ -> !mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.d("ActiveMalfunctionsListFragmentViewModel.init() " +
                        "device was rotated after fragment start and no wifi. page: ${it.page}") }

        Observable.merge(rotatedHasWifi, notRotatedNoWifi, rotatedNoWifi)
                .flatMap { (latlon, radius, page) ->
                    val repoResultObservable = mDamageClaimRepo.findWithinBBox(latlon.latitude, latlon.longitude, radius, page)
                            .map { DamageClaimsResponse(it.toMutableList(), ErrorCode.Remote.REC_OK) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), Observable.just(radius),
                            Observable.just(page), repoResultObservable, { o1, o2, o3, o4 -> IsRepoEmptyDTO(o1, o2, o3, o4) })
                }
                .flatMap { (latlon, radius, page, response) ->
                    if (response.damageClaims.size >= itemsPerPage) {
                        return@flatMap Observables.zip(Observable.just(latlon), Observable.just(response))
                    }

                    val remainder = itemsPerPage - response.damageClaims.size
                    val serverResponseObservable = mApiClient.getDamageClaims(latlon.latitude, latlon.longitude, radius, page, remainder)
                            .doOnSuccess { serverResponse -> mDamageClaimRepo.saveAll(serverResponse.damageClaims) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), Observable.just(response),
                            serverResponseObservable, { _latlon, repoResponse, serverResponse ->
                        serverResponse.damageClaims.addAll(repoResponse.damageClaims)

                        return@zip Pair(_latlon, serverResponse)
                    })
                }
                .subscribe(mEitherFromRepoOrServerSubject)
    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("ActiveMalfunctionsListFragmentViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    override fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long) {
        mRequestParamsSubject.onNext(GetDamageClaimsRequestParamsDTO(latLng, radius, page))
    }

    private fun handleResponse(response: DamageClaimResponseWithDistanceDTO) {
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            handleBadResponse(response.errorCode)
            return
        }

        mOnDamageClaimsPageReceivedSubject.onNext(response.damageClaims)
    }

    private fun handleBadResponse(errorCode: ErrorCode.Remote) {
        when (errorCode) {
            ErrorCode.Remote.REC_NOTHING_FOUND -> mOnNothingFoundSubject.onNext(Unit)

            else -> throw RuntimeException("Unknown errorCode: $errorCode")
        }
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)
        mOnUnknownErrorSubject.onNext(error)
    }

    private fun calcDistances(lat: Double, lon: Double, response: DamageClaimsResponse): DamageClaimResponseWithDistanceDTO {
        val retVal = DamageClaimResponseWithDistanceDTO(arrayListOf(), response.errorCode)

        for (damageClaim in response.damageClaims) {
            val dist = MathUtils.distance(lat, lon, damageClaim.lat, damageClaim.lon)
            val dcwd = DamageClaimsWithDistanceDTO(MathUtils.round(dist, 2), damageClaim)
            retVal.damageClaims.add(dcwd)
        }

        retVal.damageClaims.sortWith(DistanceComparator())
        return retVal
    }

    override fun isFirstFragmentStartSubject(): BehaviorSubject<Boolean> = mIsFirstFragmentStartSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
    override fun onDamageClaimsPageReceived(): Observable<ArrayList<DamageClaimsWithDistanceDTO>> = mOnDamageClaimsPageReceivedSubject
    override fun onNothingFoundSubject(): Observable<Unit> = mOnNothingFoundSubject

    data class IsRepoEmptyDTO(val latlon: LatLng,
                              val radius: Double,
                              val page: Long,
                              val response: DamageClaimsResponse)

    inner class GetDamageClaimsRequestParamsDTO(val latlon: LatLng,
                                                val radius: Double,
                                                val page: Long) {

        operator fun component1() = latlon
        operator fun component2() = radius
        operator fun component3() = page
    }

    inner class DamageClaimResponseWithDistanceDTO(var damageClaims: ArrayList<DamageClaimsWithDistanceDTO>,
                                                   val errorCode: ErrorCode.Remote)

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