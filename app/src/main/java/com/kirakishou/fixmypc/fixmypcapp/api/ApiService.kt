package com.kirakishou.fixmypc.fixmypcapp.api

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by kirakishou on 7/22/2017.
 */
interface ApiService {

    @POST("/v1/api/login")
    fun doTest(@Body params: TestRequestParams): Single<ServiceAnswer>
}