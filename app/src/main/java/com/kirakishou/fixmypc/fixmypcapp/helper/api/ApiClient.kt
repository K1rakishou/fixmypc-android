package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * Created by kirakishou on 7/23/2017.
 */
interface ApiClient {
    fun loginRequest(loginRequest: LoginRequest): Single<LoginResponse>

    fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                 uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>): Single<MalfunctionResponse>

    fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long): Single<DamageClaimsResponse>
}