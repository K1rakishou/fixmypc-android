package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.DamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
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
    @POST("/v1/api/damage_claim_request/create")
    fun sendMalfunctionRequest(@Header("session_id") sessionId: String,
                               @Part photos: List<MultipartBody.Part>,
                               @Part("request") requestBody: DamageClaimPacket,
                               @Part("images_type") imagesType: Int): Single<Response<StatusResponse>>

    @GET("/v1/api/damage_claim_request/get_within/{lat}/{lon}/{radius}/{skip}/{count}")
    fun getDamageClaims(@Path("lat") lat: Double,
                        @Path("lon") lon: Double,
                        @Path("radius") radius: Double,
                        @Path("skip") skip: Long,
                        @Path("count") count: Long): Single<Response<DamageClaimsResponse>>

    @GET("/v1/api/profile/{user_id}")
    fun getClientProfile(@Path("user_id") userId: Long): Single<Response<ClientProfileResponse>>

    @POST("/v1/api/damage_claim_request/respond")
    fun respondToDamageClaim(@Header("session_id") sessionId: String,
                             @Body packet: RespondToDamageClaimPacket): Single<Response<RespondToDamageClaimResponse>>

    @GET("/v1/api/damage_claim_request/respond/{damage_claim_id}")
    fun checkAlreadyRespondedToDamageClaim(@Header("session_id") sessionId: String,
                                           @Path("damage_claim_id") damageClaimId: Long): Single<Response<HasAlreadyRespondedResponse>>
}