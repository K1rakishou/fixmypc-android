package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by kirakishou on 9/12/2017.
 */
class LoginRequest(protected val mLoginPacket: LoginPacket,
                   protected val mApiService: ApiService,
                   protected val mGson: Gson) : AbstractRequest<Single<LoginResponse>> {

    override fun execute(): Single<LoginResponse> {
        return mApiService.doLogin(mLoginPacket)
                .lift(OnApiErrorSingle(mGson))
                .subscribeOn(Schedulers.io())
    }
}