package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorObservable
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.AssignSpecialistPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.AssignSpecialistResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmptyException
import io.reactivex.Observable
import io.reactivex.Single
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 10/2/2017.
 */
class AssignSpecialistRequest(protected val packet: AssignSpecialistPacket,
                              protected val mApiService: ApiService,
                              protected val mAppSettings: AppSettings,
                              protected val mGson: Gson,
                              protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<AssignSpecialistResponse>>() {

    override fun build(): Single<AssignSpecialistResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmptyException()
        }

        return mApiService.assignSpecialist(mAppSettings.loadUserInfo().sessionId, packet)
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

    private fun reLoginAndResendRequest(): Single<AssignSpecialistResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmptyException()
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
                    return@flatMap mApiService.assignSpecialist(mAppSettings.loadUserInfo().sessionId, packet)
                            .toObservable()
                }
                .lift<AssignSpecialistResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { AssignSpecialistResponse(it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(AssignSpecialistResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
    }

    private fun exceptionToErrorCode(error: Throwable): Single<AssignSpecialistResponse> {
        logError(error)

        val response = when (error) {
            is ApiException -> AssignSpecialistResponse(error.errorCode)
            is TimeoutException -> AssignSpecialistResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> AssignSpecialistResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> AssignSpecialistResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }
}