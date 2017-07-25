package com.kirakishou.fixmypc.fixmypcapp.api.retrofit

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.response.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by kirakishou on 7/22/2017.
 */
interface ApiService {
    @POST("/v1/api/login")
    fun doLogin(@Body request: LoginRequest): Single<LoginResponse>

}