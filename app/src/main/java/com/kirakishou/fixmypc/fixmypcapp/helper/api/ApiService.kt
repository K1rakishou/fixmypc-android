package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.DamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by kirakishou on 7/22/2017.
 */
interface ApiService {
    @POST("/v1/api/login")
    fun doLogin(@Body packet: LoginPacket): Single<Response<LoginResponse>>

    @Multipart
    @POST("/v1/api/damage_claim_request")
    fun sendMalfunctionRequest(@Header("session_id") sessionId: String,
                               @Part photos: List<MultipartBody.Part>,
                               @Part("request") requestBody: DamageClaimPacket,
                               @Part("images_type") imagesType: Int): Single<Response<DamageClaimResponse>>

    @GET("/v1/api/damage_claim_request/{lat}/{lon}/{radius}/{page}")
    fun getDamageClaims(@Path("lat") lat: Double,
                        @Path("lon") lon: Double,
                        @Path("radius") radius: Double,
                        @Path("page") page: Long): Single<Response<DamageClaimsResponse>>
}