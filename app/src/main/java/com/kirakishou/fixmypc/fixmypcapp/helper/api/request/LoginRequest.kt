package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import io.reactivex.Single
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

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
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun exceptionToErrorCode(error: Throwable): Single<LoginResponse> {
        val response = when (error) {
            is ApiException -> LoginResponse("", AccountType.Guest, error.errorCode)
            is TimeoutException -> LoginResponse("", AccountType.Guest, ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> LoginResponse("", AccountType.Guest, ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> LoginResponse("", AccountType.Guest, ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }
}