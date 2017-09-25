package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.RespondToDamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 9/25/2017.
 */
class RespondToDamageClaimRequest(protected val packet: RespondToDamageClaimPacket,
                                  protected val mApiService: ApiService,
                                  protected val mAppSettings: AppSettings,
                                  protected val mGson: Gson) : AbstractRequest<Single<RespondToDamageClaimResponse>> {

    override fun execute(): Single<RespondToDamageClaimResponse> {
        return mApiService.respondToDamageClaim(mAppSettings.userInfo.get().sessionId, packet)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun exceptionToErrorCode(error: Throwable): Single<RespondToDamageClaimResponse> {
        val response = when (error) {
            is ApiException -> StatusResponse(error.errorCode)
            is TimeoutException -> StatusResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> StatusResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> StatusResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response as RespondToDamageClaimResponse)
    }
}