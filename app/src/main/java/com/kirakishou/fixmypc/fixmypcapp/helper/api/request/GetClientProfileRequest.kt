package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.ClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 9/20/2017.
 */
class GetClientProfileRequest(protected val mUserId: Long,
                              protected val mApiService: ApiService,
                              protected val mGson: Gson) : AbstractRequest<Single<ClientProfileResponse>> {

    override fun execute(): Single<ClientProfileResponse> {
        return mApiService.getClientProfile(mUserId)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun exceptionToErrorCode(error: Throwable): Single<ClientProfileResponse> {
        val response = when (error) {
            is ApiException -> StatusResponse(error.errorCode)
            is TimeoutException -> StatusResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> StatusResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> StatusResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response as ClientProfileResponse)
    }
}