package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.CreateDamageClaimRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.GetDamageClaimRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */

class ApiClientImpl
@Inject constructor(protected val mWifiUtils: WifiUtils,
                    protected val mApiService: ApiService,
                    protected val mAppSettings: AppSettings,
                    protected val mGson: Gson,
                    protected val mDamageClaimRepo: DamageClaimRepository) : ApiClient {

    override fun loginRequest(loginPacket: LoginPacket): Single<LoginResponse> {
        //loginRequest is a light-weight request so we can always do it over network
        return LoginRequest(loginPacket, mApiService, mGson)
                .execute()
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse> {
        if (mWifiUtils.isWifiConnected()) {
            Timber.d("createMalfunctionRequest() Making a request to the server")
            return CreateDamageClaimRequest(damageClaimInfo, uploadProgressUpdateSubject, mApiService, mAppSettings, mGson)
                    .execute()
        } else {
            Timber.d("createMalfunctionRequest() Returning a warning")
            return Single.just(StatusResponse(ErrorCode.Remote.REC_WIFI_IS_NOT_CONNECTED))
        }
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long): Single<DamageClaimsResponse> {
        if (mWifiUtils.isWifiConnected()) {
            Timber.d("getDamageClaims() Fetching data from the server")
            return GetDamageClaimRequest(lat, lon, radius, page, mApiService, mGson)
                    .execute()
                    .doOnSuccess { response -> mDamageClaimRepo.saveAll(response.damageClaims) }
        } else {
            //TODO: Load from repository
            Timber.d("getDamageClaims() Retrieving data from the repository")
            return mDamageClaimRepo.findWithinBBox(lat, lon, radius, page)
                    .subscribeOn(Schedulers.io())
                    .map { DamageClaimsResponse(it, ErrorCode.Remote.REC_OK) }
                    .first(DamageClaimsResponse(emptyList(), ErrorCode.Remote.REC_EMPTY_REPOSITORY))
        }
    }
}










