package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
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
import java.util.concurrent.TimeUnit
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

    private val mCompositeDisposable = CompositeDisposable()

    private lateinit var mIsFirstFragmentStartSubject: BehaviorSubject<Boolean>
    private lateinit var mRequestParamsSubject: BehaviorSubject<GetDamageClaimsRequestParamsDTO>
    private lateinit var mOnDamageClaimsPageReceivedSubject: BehaviorSubject<ArrayList<DamageClaimsWithDistanceDTO>>
    private lateinit var mOnNothingFoundSubject: BehaviorSubject<Unit>
    private lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mCompositeDisposable.clear()

        mIsFirstFragmentStartSubject = BehaviorSubject.create<Boolean>()
        mRequestParamsSubject = BehaviorSubject.create<GetDamageClaimsRequestParamsDTO>()
        mOnDamageClaimsPageReceivedSubject = BehaviorSubject.create<ArrayList<DamageClaimsWithDistanceDTO>>()
        mOnNothingFoundSubject = BehaviorSubject.create<Unit>()
        mOnUnknownErrorSubject = BehaviorSubject.create<Throwable>()

        //multicast params and rotation state to three different observables
        val locationAndIsFirstStartObservable = Observables.combineLatest(mRequestParamsSubject, mIsFirstFragmentStartSubject)
                .subscribeOn(Schedulers.io())
                .publish()
                .autoConnect(3)

        //1. has wifi and device was not rotated since fragment start
        mCompositeDisposable += locationAndIsFirstStartObservable
                .filter { (_, isFirstFragmentStart) -> isFirstFragmentStart }
                .filter { _ -> mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext {
                    Timber.e("Fetching damage claims from  the server with coordinates " +
                            "[lat:${it.latlon.latitude}, lon:${it.latlon.longitude}], page: ${it.page}")
                }
                .flatMap { (latlon, radius, page) ->
                    val responseObservable = mApiClient.getDamageClaims(latlon.latitude, latlon.longitude, radius, page)
                            .doOnSuccess { response -> mDamageClaimRepo.saveAll(response.damageClaims) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), responseObservable)
                }
                .map { (latlon, response) -> calcDistances(latlon.latitude, latlon.longitude, response) }
                .delay(1, TimeUnit.SECONDS)
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        //2. has wifi but device was rotated since fragment start
        val rotatedAndWifiObservable = locationAndIsFirstStartObservable
                .subscribeOn(Schedulers.io())
                .filter { (_, isFirstFragmentStart) -> !isFirstFragmentStart }
                .filter { _ -> mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.e("has wifi but device was rotated since fragment start. page: ${it.page}") }

        //3. device was not rotated after fragment start but no wifi
        val notRotatedAndNoWifiObservable = locationAndIsFirstStartObservable
                .subscribeOn(Schedulers.io())
                .filter { (_, isFirstFragmentStart) -> isFirstFragmentStart }
                .filter { _ -> !mWifiUtils.isWifiConnected() }
                .map { it.first }
                .doOnNext { Timber.e("device was not rotated after fragment start but no wifi. page: ${it.page}") }

        //2 and 3 are mutually exclusive so we can merge them into one.
        //if device has been rotated OR we don't have wifi connection - get data from repository
        mCompositeDisposable += Observable.merge(rotatedAndWifiObservable, notRotatedAndNoWifiObservable)
                .flatMap { (latlon, radius, page) ->
                    val repoResultObservable = mDamageClaimRepo.findWithinBBox(latlon.latitude, latlon.longitude, radius, page)
                            .map { DamageClaimsResponse(it, ErrorCode.Remote.REC_OK) }
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), repoResultObservable)
                }
                .map { (latlon, response) -> calcDistances(latlon.latitude, latlon.longitude, response) }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
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