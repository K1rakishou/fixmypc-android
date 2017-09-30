package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.*
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.RespondToDamageClaimPacket
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
                    protected val mGson: Gson,
                    protected val mSchedulers: SchedulerProvider) : ApiClient {

    override fun loginRequest(packet: LoginPacket): Single<LoginResponse> {
        return LoginRequest(packet, mApiService, mGson, mSchedulers)
                .execute()
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse> {
        return CreateDamageClaimRequest(damageClaimInfo, uploadProgressUpdateSubject, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, skip: Long, count: Long): Single<DamageClaimsResponse> {
        return GetDamageClaimRequest(lat, lon, radius, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun getClientProfile(userId: Long): Single<ClientProfileResponse> {
        return GetClientProfileRequest(userId, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }

    override fun respondToDamageClaim(packet: RespondToDamageClaimPacket): Single<RespondToDamageClaimResponse> {
        return RespondToDamageClaimRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }

    override fun checkAlreadyRespondedToDamageClaim(damageClaimId: Long): Single<HasAlreadyRespondedResponse> {
        return CheckAlreadyRespondedToDamageClaimRequest(damageClaimId, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }

    override fun getClientDamageClaimsPaged(isActive: Boolean, skip: Long, count: Long): Single<DamageClaimsResponse> {
        return GetClientDamageClaimsPagedRequest(isActive, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }

    override fun getRespondedSpecialistsPaged(damageClaimId: Long, skip: Long, count: Long): Single<SpecialistsListResponse> {
        return GetRespondedSpecialistsPagedRequest(damageClaimId, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .execute()
    }
}




































