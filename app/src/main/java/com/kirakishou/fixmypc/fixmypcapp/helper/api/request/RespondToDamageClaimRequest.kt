package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorObservable
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.RespondToDamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmpty
import io.reactivex.Observable
import io.reactivex.Single
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 9/25/2017.
 */
class RespondToDamageClaimRequest(protected val packet: RespondToDamageClaimPacket,
                                  protected val mApiService: ApiService,
                                  protected val mAppSettings: AppSettings,
                                  protected val mGson: Gson,
                                  protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<RespondToDamageClaimResponse>> {

    override fun execute(): Single<RespondToDamageClaimResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmpty()
        }

        return mApiService.respondToDamageClaim(mAppSettings.loadUserInfo().sessionId, packet)
                .subscribeOn(mSchedulers.provideIo())
                .lift(OnApiErrorSingle(mGson))
                .flatMap { response ->
                    if (response.errorCode == ErrorCode.Remote.REC_SESSION_ID_EXPIRED) {
                        return@flatMap reLoginAndResendRequest()
                    }

                    return@flatMap Single.just(response)
                }
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun reLoginAndResendRequest(): Single<RespondToDamageClaimResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmpty()
        }

        val userInfo = mAppSettings.loadUserInfo()

        val loginResponseObservable = mApiService.doLogin(LoginPacket(userInfo.login, userInfo.password))
                .subscribeOn(mSchedulers.provideIo())
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
                .publish()
                .autoConnect(2)

        val successObservable = loginResponseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_OK }
                .doOnNext { mAppSettings.updateSessionId(it.sessionId) }
                .flatMap {
                    return@flatMap mApiService.respondToDamageClaim(mAppSettings.loadUserInfo().sessionId, packet).toObservable()
                }
                .lift<RespondToDamageClaimResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { RespondToDamageClaimResponse(it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(StatusResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR) as RespondToDamageClaimResponse)
    }

    private fun exceptionToErrorCode(error: Throwable): Single<RespondToDamageClaimResponse> {
        val response = when (error) {
            is ApiException -> RespondToDamageClaimResponse(error.errorCode)
            is TimeoutException -> RespondToDamageClaimResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> RespondToDamageClaimResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> RespondToDamageClaimResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }
}