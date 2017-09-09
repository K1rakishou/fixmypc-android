package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.MalfunctionResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by kirakishou on 7/22/2017.
 */
interface ApiService {
    @POST("/v1/api/login")
    fun doLogin(@Body request: LoginRequest): Single<Response<LoginResponse>>

    @Multipart
    @POST("/v1/api/m_request")
    fun sendMalfunctionRequest(@Header("session_id") sessionId: String,
                               @Part photos: List<MultipartBody.Part>,
                               @Part("request") requestBody: MalfunctionRequest,
                               @Part("images_type") imagesType: Int): Single<Response<MalfunctionResponse>>

    @GET("/v1/api/damage_claim_request/{lat}/{lon}/{radius}/{page}")
    fun getDamageClaims(@Path("lat") lat: Double,
                        @Path("lon") lon: Double,
                        @Path("radius") radius: Double,
                        @Path("page") page: Long): Single<Response<DamageClaimsResponse>>
}