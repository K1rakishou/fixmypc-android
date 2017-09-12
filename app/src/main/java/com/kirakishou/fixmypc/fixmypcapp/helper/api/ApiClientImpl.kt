package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.CreateDamageClaimRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.GetDamageClaimRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */

class ApiClientImpl
@Inject constructor(protected val mApiService: ApiService,
                    protected val mAppSettings: AppSettings,
                    protected val mGson: Gson,
                    protected val mDamageClaimRepo: DamageClaimRepository) : ApiClient {

    override fun loginRequest(loginPacket: LoginPacket): Single<LoginResponse> {
        return LoginRequest(loginPacket, mApiService, mGson).execute()
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse> {
        return CreateDamageClaimRequest(damageClaimInfo, uploadProgressUpdateSubject, mApiService, mAppSettings, mGson).execute()
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long): Single<DamageClaimsResponse> {
        return GetDamageClaimRequest(lat, lon, radius, page, mApiService, mGson).execute()
                .doOnSuccess { response -> mDamageClaimRepo.saveAll(response.damageClaims) }
    }
}










