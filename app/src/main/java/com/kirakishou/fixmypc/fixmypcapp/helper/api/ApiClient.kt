package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject

/**
 * Created by kirakishou on 7/23/2017.
 */
interface ApiClient {
    fun loginRequest(loginPacket: LoginPacket): Single<LoginResponse>

    fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                 uploadProgressUpdateSubject: ReplaySubject<ProgressUpdate>): Single<StatusResponse>

    fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long): Single<DamageClaimsResponse>
}