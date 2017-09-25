package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject

/**
 * Created by kirakishou on 7/23/2017.
 */
interface ApiClient {
    fun loginRequest(packet: LoginPacket): Single<LoginResponse>

    fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                 uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse>

    fun getDamageClaims(lat: Double, lon: Double, radius: Double, skip: Long, count: Long): Single<DamageClaimsResponse>

    fun getClientProfile(userId: Long): Single<ClientProfileResponse>

    fun respondToDamageClaim(packet: RespondToDamageClaimPacket): Single<RespondToDamageClaimResponse>
}