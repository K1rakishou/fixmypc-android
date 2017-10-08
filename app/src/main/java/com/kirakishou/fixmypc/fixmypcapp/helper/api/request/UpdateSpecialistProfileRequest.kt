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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.SpecialistProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.BadServerResponseException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UserInfoIsEmpty
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
class UpdateSpecialistProfileRequest(protected val photoPath: String,
                                     protected val packet: SpecialistProfilePacket,
                                     protected val mApiService: ApiService,
                                     protected val mAppSettings: AppSettings,
                                     protected val mGson: Gson,
                                     protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<UpdateSpecialistProfileResponse>> {

    override fun execute(): Single<UpdateSpecialistProfileResponse> {
        return Single.just(Params(photoPath, packet))
                .map {
                    val photoBody = prepareRequest(it.photoPath)
                    return@map photoBody to it.packet
                }
                .flatMap { params ->
                    if (!mAppSettings.isUserInfoExists()) {
                        throw UserInfoIsEmpty()
                    }

                    mApiService.updateSpecialistProfile(mAppSettings.loadUserInfo().sessionId, params.first, params.second)
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

    private fun reLoginAndResendRequest(): Single<UpdateSpecialistProfileResponse> {
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
                    val photoBody = prepareRequest(photoPath)

                    return@flatMap mApiService.updateSpecialistProfile(mAppSettings.loadUserInfo().sessionId, photoBody, packet)
                            .toObservable()
                }
                .lift<UpdateSpecialistProfileResponse>(OnApiErrorObservable(mGson))

        val failObservable = loginResponseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK }
                .doOnNext { throw CouldNotUpdateSessionId() }
                .map { UpdateSpecialistProfileResponse(it.errorCode) }

        return Observable.merge(successObservable, failObservable)
                .single(UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
    }

    private fun exceptionToErrorCode(error: Throwable): Single<UpdateSpecialistProfileResponse> {
        val response = when (error) {
            is ApiException -> UpdateSpecialistProfileResponse(error.errorCode)
            is TimeoutException -> UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is BadServerResponseException -> UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)
            is FileSizeExceededException -> UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED)
            is SelectedPhotoDoesNotExistsException -> UpdateSpecialistProfileResponse(ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS)

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

    data class Params(val photoPath: String,
                      val packet: SpecialistProfilePacket)
}