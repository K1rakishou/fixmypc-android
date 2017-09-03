package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject

/**
 * Created by kirakishou on 7/23/2017.
 */
interface ApiClient {
    fun loginRequest(loginRequest: LoginRequest,
                     responseSubject: SingleSubject<Pair<LoginRequest, LoginResponse>>)

    fun createMalfunctionRequest(malfunctionRequestInfo: MalfunctionRequestInfo,
                                 uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>): Single<MalfunctionResponse>
}