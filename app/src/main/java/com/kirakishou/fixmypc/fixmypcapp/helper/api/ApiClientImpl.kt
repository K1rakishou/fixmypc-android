package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.request.*
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
import io.reactivex.Single
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
                .build()
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo): Single<StatusResponse> {
        return CreateDamageClaimRequest(damageClaimInfo, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, skip: Long, count: Long): Single<DamageClaimsResponse> {
        return GetDamageClaimRequest(lat, lon, radius, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun getClientProfile(): Single<ClientProfileResponse> {
        return GetClientProfileRequest(mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun respondToDamageClaim(packet: RespondToDamageClaimPacket): Single<RespondToDamageClaimResponse> {
        return RespondToDamageClaimRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun checkAlreadyRespondedToDamageClaim(damageClaimId: Long): Single<HasAlreadyRespondedResponse> {
        return CheckAlreadyRespondedToDamageClaimRequest(damageClaimId, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun getClientDamageClaimsPaged(isActive: Boolean, skip: Long, count: Long): Single<DamageClaimsWithRespondedSpecialistsResponse> {
        return GetClientDamageClaimsPagedRequest(isActive, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun getRespondedSpecialistsPaged(damageClaimId: Long, skip: Long, count: Long): Single<SpecialistsListResponse> {
        return GetRespondedSpecialistsPagedRequest(damageClaimId, skip, count, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
                .delay(1, TimeUnit.SECONDS)
    }

    override fun assignSpecialist(packet: AssignSpecialistPacket): Single<AssignSpecialistResponse> {
        return AssignSpecialistRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun getAssignedSpecialist(damageClaimId: Long): Single<AssignedSpecialistResponse> {
        return GetAssignedSpecialistRequest(damageClaimId, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun getSpecialistProfile(): Single<SpecialistProfileResponse> {
        return GetSpecialistProfileRequest(mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun getSpecialistProfileById(specialistUserId: Long): Single<SpecialistProfileResponse> {
        return GetSpecialistProfileByIdRequest(specialistUserId, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun updateSpecialistProfilePhoto(photoPath: String): Single<UpdateSpecialistProfilePhotoResponse> {
        return UpdateSpecialistProfilePhotoRequest(photoPath, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun updateSpecialistProfileInfo(packet: SpecialistProfilePacket): Single<UpdateSpecialistProfileInfoResponse> {
        return UpdateSpecialistProfileInfoRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun isSpecialistProfileFilledIn(): Single<IsProfileFilledInResponse> {
        return IsSpecialistProfileFilledInRequest(mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun updateClientProfile(packet: ClientProfilePacket): Single<UpdateClientProfileResponse> {
        return UpdateClientProfileRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun isClientProfileFilledIn(): Single<IsProfileFilledInResponse> {
        return IsClientProfileFilledIn(mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }

    override fun markResponseViewed(packet: MarkResponseViewedPacket): Single<MarkResponseViewedResponse> {
        return MarkResponseViewedRequest(packet, mApiService, mAppSettings, mGson, mSchedulers)
                .build()
    }
}




































