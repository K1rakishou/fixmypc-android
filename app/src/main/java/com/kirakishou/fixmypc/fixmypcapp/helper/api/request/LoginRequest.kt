package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import io.reactivex.Single

/**
 * Created by kirakishou on 9/12/2017.
 */
class LoginRequest(protected val mLoginPacket: LoginPacket,
                   protected val mApiService: ApiService,
                   protected val mGson: Gson,
                   protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<LoginResponse>> {

    override fun execute(): Single<LoginResponse> {
        return mApiService.doLogin(mLoginPacket)
                .subscribeOn(mSchedulers.provideIo())
                .lift(OnApiErrorSingle(mGson))
    }
}