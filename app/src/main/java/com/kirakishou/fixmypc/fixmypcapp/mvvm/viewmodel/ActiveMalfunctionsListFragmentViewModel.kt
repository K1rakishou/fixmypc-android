package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
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
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class ActiveMalfunctionsListFragmentViewModel
@Inject constructor(protected val mApiClient: ApiClient) : BaseViewModel(),
        ActiveMalfunctionsListFragmentInputs,
        ActiveMalfunctionsListFragmentOutputs,
        ActiveMalfunctionsListFragmentErrors {

    val mInputs: ActiveMalfunctionsListFragmentInputs = this
    val mOutputs: ActiveMalfunctionsListFragmentOutputs = this
    val mErrors: ActiveMalfunctionsListFragmentErrors = this

    private val mCompositeDisposable = CompositeDisposable()
    private val mLocationSubject = BehaviorSubject.create<GetDamageClaimsRequestParamsDTO>()
    private val mOnDamageClaimsPageReceivedSubject = BehaviorSubject.create<ArrayList<DamageClaimsWithDistanceDTO>>()
    private val mOnNothingFoundSubject = BehaviorSubject.create<Unit>()
    private val mOnUnknownErrorSubject = BehaviorSubject.create<Throwable>()

    init {
        mCompositeDisposable += mLocationSubject
                .subscribeOn(Schedulers.io())
                .flatMap { (latlon, radius, page) ->
                    Timber.d("Fetching damage claims from  the server with coordinates [lat:${latlon.latitude}, lon:${latlon.longitude}]")

                    val responseObservable = mApiClient.getDamageClaims(latlon.latitude, latlon.longitude, radius, page)
                            .toObservable()

                    return@flatMap Observables.zip(Observable.just(latlon), responseObservable)
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
        mLocationSubject.onNext(GetDamageClaimsRequestParamsDTO(latLng, radius, page))
    }

    private fun handleResponse(responseDTO: DamageClaimResponseWithDistanceDTO) {
        when (responseDTO.errorCode) {
            ErrorCode.Remote.REC_OK -> mOnDamageClaimsPageReceivedSubject.onNext(responseDTO.damageClaims)
            ErrorCode.Remote.REC_NOTHING_FOUND -> mOnNothingFoundSubject.onNext(Unit)

            else -> throw RuntimeException("Unknown errorCode: ${responseDTO.errorCode}")
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