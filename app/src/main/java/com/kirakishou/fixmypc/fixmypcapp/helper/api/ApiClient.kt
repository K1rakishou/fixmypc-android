package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
import retrofit2.Response
import retrofit2.http.Header

/**
 * Created by kirakishou on 7/23/2017.
 */
interface ApiClient {
    fun loginRequest(packet: LoginPacket): Single<LoginResponse>

    fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo): Single<StatusResponse>

    fun getDamageClaims(lat: Double, lon: Double, radius: Double, skip: Long, count: Long): Single<DamageClaimsResponse>

    fun getClientProfile(): Single<ClientProfileResponse>

    fun respondToDamageClaim(packet: RespondToDamageClaimPacket): Single<RespondToDamageClaimResponse>

    fun checkAlreadyRespondedToDamageClaim(damageClaimId: Long): Single<HasAlreadyRespondedResponse>

    fun getClientDamageClaimsPaged(isActive: Boolean, skip: Long, count: Long): Single<DamageClaimsWithCountResponse>

    fun getRespondedSpecialistsPaged(damageClaimId: Long, skip: Long, count: Long): Single<SpecialistsListResponse>

    fun assignSpecialist(packet: AssignSpecialistPacket): Single<AssignSpecialistResponse>

    fun getSpecialistProfile(): Single<SpecialistProfileResponse>

    fun updateSpecialistProfilePhoto(photoPath: String): Single<UpdateSpecialistProfilePhotoResponse>

    fun updateSpecialistProfileInfo(packet: SpecialistProfilePacket): Single<UpdateSpecialistProfileInfoResponse>

    fun isSpecialistProfileFilledIn(): Single<IsProfileFilledInResponse>

    fun updateClientProfile(packet: ClientProfilePacket): Single<UpdateClientProfileResponse>

    fun isClientProfileFilledIn(): Single<IsProfileFilledInResponse>
}