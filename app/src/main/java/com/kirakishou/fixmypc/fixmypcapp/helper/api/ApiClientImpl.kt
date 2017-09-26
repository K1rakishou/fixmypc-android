package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */

class ApiClientImpl
@Inject constructor(protected val mApiService: ApiService,
                    protected val mAppSettings: AppSettings,
                    protected val mGson: Gson) : ApiClient {

    override fun loginRequest(packet: LoginPacket): Single<LoginResponse> {
        return LoginRequest(packet, mApiService, mGson)
                .execute()
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse> {
        return CreateDamageClaimRequest(damageClaimInfo, uploadProgressUpdateSubject, mApiService, mAppSettings, mGson)
                .execute()
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, skip: Long, count: Long): Single<DamageClaimsResponse> {
        return GetDamageClaimRequest(lat, lon, radius, skip, count, mApiService, mGson)
                .execute()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun getClientProfile(userId: Long): Single<ClientProfileResponse> {
        return GetClientProfileRequest(userId, mApiService, mGson)
                .execute()
    }

    override fun respondToDamageClaim(packet: RespondToDamageClaimPacket): Single<RespondToDamageClaimResponse> {
        return RespondToDamageClaimRequest(packet, mApiService, mAppSettings, mGson)
                .execute()
    }

    override fun checkAlreadyRespondedToDamageClaim(damageClaimId: Long): Single<HasAlreadyRespondedResponse> {
        return CheckAlreadyRespondedToDamageClaimRequest(damageClaimId, mApiService, mAppSettings, mGson)
                .execute()
    }
}










