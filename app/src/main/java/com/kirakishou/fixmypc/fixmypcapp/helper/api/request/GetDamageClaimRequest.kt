package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorObservable
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmpty
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 9/12/2017.
 */
class GetDamageClaimRequest(protected val mLat: Double,
                            protected val mLon: Double,
                            protected val mRadius: Double,
                            protected val mPage: Long,
                            protected val mCount: Long,
                            protected val mApiService: ApiService,
                            protected val mAppSettings: AppSettings,
                            protected val mGson: Gson) : AbstractRequest<Single<DamageClaimsResponse>> {

    override fun execute(): Single<DamageClaimsResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmpty()
        }

        return mApiService.getDamageClaims(mAppSettings.loadUserInfo().sessionId, mLat, mLon, mRadius, mPage, mCount)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
                .flatMap { response ->
                    if (response.errorCode == ErrorCode.Remote.REC_SESSION_ID_EXPIRED) {
                        return@flatMap reLoginAndResendRequest()
                    }

                    return@flatMap Single.just(response)
                }
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun reLoginAndResendRequest(): Single<DamageClaimsResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmpty()
        }

        val userInfo = mAppSettings.loadUserInfo()

        val loginResponseObservable = mApiService.doLogin(LoginPacket(userInfo.login, userInfo.password))
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
                .publish()
                .autoConnect(2)

        val successObservable = loginResponseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_OK }
                .doOnNext { mAppSettings.updateSessionId(it.sessionId) }
                .flatMap {
                    return@flatMap mApiService.getDamageClaims(mAppSettings.loadUserInfo().sessionId, mLat, mLon, mRadius, mPage, mCount).toObservable()
                }
                .lift<DamageClaimsResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { DamageClaimsResponse(mutableListOf(), it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(StatusResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR) as DamageClaimsResponse)
    }

    private fun exceptionToErrorCode(error: Throwable): Single<DamageClaimsResponse> {
        val response = when (error) {
            is ApiException -> DamageClaimsResponse(mutableListOf(), error.errorCode)
            is TimeoutException -> DamageClaimsResponse(mutableListOf(), ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> DamageClaimsResponse(mutableListOf(), ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> DamageClaimsResponse(mutableListOf(), ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }
}