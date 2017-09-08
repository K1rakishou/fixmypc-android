package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.DamageClaimsWithDistanceDTO
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.DamageClaimsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class ActiveMalfunctionsListFragmentPresenterImpl
    @Inject constructor(protected val mApiClient: ApiClient) : BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()
    private val locationSubject = BehaviorSubject.create<GetDamageClaimsRequestParamsDTO>()

    fun initPresenter() {
        Timber.d("ActiveMalfunctionsListFragmentPresenterImpl.initPresenter()")

        mCompositeDisposable += locationSubject
                .subscribeOn(Schedulers.io())
                .subscribe({ params ->
                    Timber.d("Fetching damage claims from  the server with coordinates [lat:${params.latlon.latitude}, lon:${params.latlon.longitude}]")
                    getDamageClaims(params.latlon.latitude, params.latlon.longitude, params.radius, params.page)
                }, { error ->
                    Timber.e(error)
                })
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

    fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long) {
        locationSubject.onNext(GetDamageClaimsRequestParamsDTO(latLng, radius, page))
    }

    private fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long) {
        mCompositeDisposable += mApiClient.getDamageClaims(lat, lon, radius, page)
                .map { calcDistances(lat, lon, it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    private fun calcDistances(lat: Double, lon: Double, response: DamageClaimsResponse): DamageClaimResponseWithDistanceDTO {
        val retVal = DamageClaimResponseWithDistanceDTO(arrayListOf(), response.errorCode)

        for (damageClaim in response.damageClaims) {
            val dist = MathUtils.distance(lat, lon, damageClaim.lat, damageClaim.lon)
            val dcwd =  DamageClaimsWithDistanceDTO(MathUtils.round(dist, 2), damageClaim)
            retVal.damageClaims.add(dcwd)
        }

        retVal.damageClaims.sortWith(DistanceComparator())
        return retVal
    }

    private fun handleResponse(responseDTO: DamageClaimResponseWithDistanceDTO) {
        Timber.d("items size = ${responseDTO.damageClaims.size}")

        //callbacks.onDamageClaimsPageReceived(responseDTO.damageClaims)
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)
    }

    inner class GetDamageClaimsRequestParamsDTO(val latlon: LatLng,
                                               val radius: Double,
                                               val page: Long)

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