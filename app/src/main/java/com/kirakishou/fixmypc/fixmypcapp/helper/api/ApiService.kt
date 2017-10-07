package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.AssignSpecialistPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.DamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.RespondToDamageClaimPacket
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
    @POST("/v1/api/damage_claim/create")
    fun sendMalfunctionRequest(@Header("session_id") sessionId: String,
                               @Part photos: List<MultipartBody.Part>,
                               @Part("request") requestBody: DamageClaimPacket,
                               @Part("images_type") imagesType: Int): Single<Response<StatusResponse>>

    @GET("/v1/api/damage_claim/get_within/{lat}/{lon}/{radius}/{skip}/{count}")
    fun getDamageClaims(@Header("session_id") sessionId: String,
                        @Path("lat") lat: Double,
                        @Path("lon") lon: Double,
                        @Path("radius") radius: Double,
                        @Path("skip") skip: Long,
                        @Path("count") count: Long): Single<Response<DamageClaimsResponse>>

    @GET("/v1/api/client/profile/{user_id}")
    fun getClientProfile(@Header("session_id") sessionId: String,
                         @Path("user_id") userId: Long): Single<Response<ClientProfileResponse>>

    @POST("/v1/api/damage_claim/respond")
    fun respondToDamageClaim(@Header("session_id") sessionId: String,
                             @Body packet: RespondToDamageClaimPacket): Single<Response<RespondToDamageClaimResponse>>

    @GET("/v1/api/damage_claim/respond/{damage_claim_id}")
    fun checkAlreadyRespondedToDamageClaim(@Header("session_id") sessionId: String,
                                           @Path("damage_claim_id") damageClaimId: Long): Single<Response<HasAlreadyRespondedResponse>>

    @GET("/v1/api/damage_claim/get_client/{is_active}/{skip}/{count}")
    fun getClientDamageClaimsPaged(@Header("session_id") sessionId: String,
                                   @Path("is_active") isActive: Boolean,
                                   @Path("skip") skip: Long,
                                   @Path("count") count: Long): Single<Response<DamageClaimsResponse>>

    @GET("/v1/api/specialist/profile/{damage_claim_id}/{skip}/{count}")
    fun getRespondedSpecialistsPaged(@Header("session_id") sessionId: String,
                                     @Path("damage_claim_id") damageClaimId: Long,
                                     @Path("skip") skip: Long,
                                     @Path("count") count: Long): Single<Response<SpecialistsListResponse>>

    @POST("/v1/api/specialist/assign")
    fun assignSpecialist(@Header("session_id") sessionId: String,
                         @Body packet: AssignSpecialistPacket): Single<Response<StatusResponse>>

    @GET("/v1/api/specialist/profile")
    fun getSpecialistProfile(@Header("session_id") sessionId: String): Single<Response<SpecialistProfileResponse>>
}





































