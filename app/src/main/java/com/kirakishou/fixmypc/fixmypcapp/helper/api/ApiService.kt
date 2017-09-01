package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import io.reactivex.Observable
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

    @POST("/v1/api/login")
    fun doLogin2(@Body request: LoginRequest): Observable<Response<LoginResponse>>

    @Multipart
    @POST("/v1/api/m_request")
    fun sendMalfunctionRequest(@Header("session_id") sessionId: String,
                               @Part photos: List<MultipartBody.Part>,
                               @Part("request") requestBody: MalfunctionRequest,
                               @Part("images_type") imagesType: Int): Observable<Response<MalfunctionResponse>>
}