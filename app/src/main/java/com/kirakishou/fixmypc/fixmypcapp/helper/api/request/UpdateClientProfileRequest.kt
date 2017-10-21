package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorObservable
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.ClientProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.ClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfileInfoResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmptyException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import io.reactivex.Observable
import io.reactivex.Single
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 10/20/2017.
 */
class UpdateClientProfileRequest(protected val packet: ClientProfilePacket,
                                 protected val mApiService: ApiService,
                                 protected val mAppSettings: AppSettings,
                                 protected val mGson: Gson,
                                 protected val mSchedulers: SchedulerProvider): AbstractRequest<Single<UpdateClientProfileResponse>>() {

    override fun build(): Single<UpdateClientProfileResponse> {
        return Single.just(packet)
                .flatMap {
                    if (!mAppSettings.isUserInfoExists()) {
                        throw UserInfoIsEmptyException()
                    }

                    return@flatMap mApiService.updateClientProfile(mAppSettings.loadUserInfo().sessionId, it)
                }
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

    private fun reLoginAndResendRequest(): Single<UpdateClientProfileResponse> {
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
                    return@flatMap mApiService.updateClientProfile(mAppSettings.loadUserInfo().sessionId, packet)
                            .toObservable()
                }
                .lift<UpdateClientProfileResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { UpdateClientProfileResponse(it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(UpdateClientProfileResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
    }

    private fun exceptionToErrorCode(error: Throwable): Single<UpdateClientProfileResponse> {
        logError(error)

        val response = when (error) {
            is ApiException -> UpdateClientProfileResponse(error.errorCode)
            is TimeoutException -> UpdateClientProfileResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> UpdateClientProfileResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> UpdateClientProfileResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }
}