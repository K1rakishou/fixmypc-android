package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorObservable
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfilePhotoResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmptyException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 10/8/2017.
 */
class UpdateSpecialistProfilePhotoRequest(protected val photoPath: String,
                                          protected val mApiService: ApiService,
                                          protected val mAppSettings: AppSettings,
                                          protected val mGson: Gson,
                                          protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<UpdateSpecialistProfilePhotoResponse>>() {

    override fun build(): Single<UpdateSpecialistProfilePhotoResponse> {
        return Single.just(photoPath)
                .map {
                    return@map prepareRequest(it)
                }
                .flatMap {
                    if (!mAppSettings.isUserInfoExists()) {
                        throw UserInfoIsEmptyException()
                    }

                    return@flatMap mApiService.updateSpecialistProfilePhoto(mAppSettings.loadUserInfo().sessionId, it)
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

    private fun reLoginAndResendRequest(): Single<UpdateSpecialistProfilePhotoResponse> {
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
                    val photoBody = prepareRequest(photoPath)

                    return@flatMap mApiService.updateSpecialistProfilePhoto(mAppSettings.loadUserInfo().sessionId, photoBody)
                            .toObservable()
                }
                .lift<UpdateSpecialistProfilePhotoResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { UpdateSpecialistProfilePhotoResponse(it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
    }

    private fun exceptionToErrorCode(error: Throwable): Single<UpdateSpecialistProfilePhotoResponse> {
        logError(error)

        val response = when (error) {
            is ApiException -> UpdateSpecialistProfilePhotoResponse(error.errorCode)
            is TimeoutException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)
            is FileSizeExceededException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED)
            is SelectedPhotoDoesNotExistsException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS)
            is UserInfoIsEmptyException -> UpdateSpecialistProfilePhotoResponse(ErrorCode.Remote.REC_USER_INFO_IS_EMPTY)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }

    private fun prepareRequest(photoPath: String): MultipartBody.Part {
        val photoFile = File(photoPath)
        if (photoFile.length() > Constant.MAX_FILE_SIZE) {
            throw FileSizeExceededException()
        }

        if (!photoFile.isFile || !photoFile.exists()) {
            throw SelectedPhotoDoesNotExistsException()
        }

        val body = RequestBody.create(MediaType.parse("image/*"), photoFile)
        return MultipartBody.Part.createFormData("photo", photoFile.name, body)
    }
}