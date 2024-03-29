package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ImageType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.DamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.PhotosAreNotSelectedException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 9/12/2017.
 */
class CreateDamageClaimRequest(protected val mDamageClaimInfo: DamageClaimInfo,
                               protected val mApiService: ApiService,
                               protected val mAppSettings: AppSettings,
                               protected val mGson: Gson,
                               protected val mSchedulers: SchedulerProvider) : AbstractRequest<Single<StatusResponse>>() {

    override fun build(): Single<StatusResponse> {
        //create MultipartFile bodies, check if user has selected the same file twice
        val progressBodyListObservable = Observable.fromIterable(mDamageClaimInfo.damageClaimPhotos)
                .subscribeOn(mSchedulers.provideIo())
                .map { prepareRequest(it) }
                //it.second is md5 string of the selected file
                //FIXME: Fix ErrorOnDuplicate operator
                //.lift(ErrorOnDuplicate<Pair<MultipartBody.Part, String>, String> { it.second })
                //we don't need md5 anymore
                .map { it.first }
                .toList()
                .toObservable()
                .publish()
                .autoConnect(2)

        val requestObservable = Observable.just(mDamageClaimInfo)

        //send request
        val responseObservable = Observables.zip(progressBodyListObservable, requestObservable, { a, b -> Pair(a, b) })
                .flatMap { sendRequest(it) }
                .onErrorResumeNext(Function {
                    if (it is ApiException) {
                        return@Function Observable.just(StatusResponse(it.errorCode))
                    }

                    return@Function Observable.error(it)
                })
                .publish()
                .autoConnect(3)

        //if there were no errors - do nothing
        val firstAttemptOkObservable = responseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_OK }

        //if there were REC_SESSION_ID_EXPIRED error - try to re login
        val sessionIdObservable = responseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_SESSION_ID_EXPIRED }
                .flatMap { reLogin() }
                //update sessionId with the new one
                .doOnNext { mAppSettings.updateSessionId(it) }

        //if there were some other error - do nothing
        val firstAttemptErrorObservable = responseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK && it.errorCode != ErrorCode.Remote.REC_SESSION_ID_EXPIRED }

        //re send the request
        val secondAttemptObservable = Observables.zip(sessionIdObservable, progressBodyListObservable, requestObservable, { a, b, c -> Triple(a, b, c) })
                .flatMap { resendRequest(it) }

        //now we have three different outcomes:
        //either request was accepted by the server on the first try and returned REC_OK,
        //either request was not accepted by the server for some reason and returned some error code
        //or request was not accepted by the server BECAUSE user's sessionId was removed.
        //We are merging all of these three possibilities and retrieving the first observable (and the only one, we should only have ONE observable at this point)
        return Observable.merge(firstAttemptOkObservable, firstAttemptErrorObservable, secondAttemptObservable)
                .single(StatusResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
                //we don't want to end the reactive stream if some known error has happened
                .onErrorResumeNext { error -> exceptionToErrorCode(error) }
    }

    private fun exceptionToErrorCode(error: Throwable): Single<StatusResponse> {
        logError(error)

        val response = when (error) {
            is ApiException -> StatusResponse(error.errorCode)
            is TimeoutException -> StatusResponse(ErrorCode.Remote.REC_TIMEOUT)
            is UnknownHostException -> StatusResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
            is FileSizeExceededException -> StatusResponse(ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED)
            is PhotosAreNotSelectedException -> StatusResponse(ErrorCode.Remote.REC_NO_PHOTOS_WERE_SELECTED_TO_UPLOAD)
            is SelectedPhotoDoesNotExistsException -> StatusResponse(ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS)
            is ResponseBodyIsEmpty -> StatusResponse(ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY)
            is DuplicateEntryException -> StatusResponse(ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION)
            is BadServerResponseException -> StatusResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)

            else -> throw RuntimeException("Unknown exception")
        }

        return Single.just(response)
    }

    private fun resendRequest(it: Triple<String, List<MultipartBody.Part>, DamageClaimInfo>): Observable<StatusResponse> {
        val request = DamageClaimPacket(it.third.damageClaimCategory.ordinal, it.third.damageClaimDescription,
                it.third.damageClaimLocation.latitude, it.third.damageClaimLocation.longitude)

        return mApiService.createDamageClaim(it.first, it.second, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
    }

    private fun reLogin(): Observable<String> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmptyException()
        }

        val userInfo = mAppSettings.loadUserInfo()
        return mApiService.doLogin(LoginPacket(userInfo.login, userInfo.password))
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
                .map {
                    if (it.errorCode != ErrorCode.Remote.REC_OK) {
                        throw CouldNotUpdateSessionId()
                    }

                    return@map it.sessionId
                }
    }

    private fun sendRequest(it: Pair<List<MultipartBody.Part>, DamageClaimInfo>): Observable<StatusResponse> {
        if (!mAppSettings.isUserInfoExists()) {
            throw UserInfoIsEmptyException()
        }

        val sessionId = mAppSettings.loadUserInfo().sessionId
        val request = DamageClaimPacket(it.second.damageClaimCategory.ordinal, it.second.damageClaimDescription,
                it.second.damageClaimLocation.latitude, it.second.damageClaimLocation.longitude)

        return mApiService.createDamageClaim(sessionId, it.first, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
    }

    private fun prepareRequest(photoPath: String): Pair<MultipartBody.Part, String> {
        val photoFile = File(photoPath)
        if (photoFile.length() > Constant.MAX_FILE_SIZE) {
            throw FileSizeExceededException()
        }

        if (!photoFile.isFile || !photoFile.exists()) {
            throw SelectedPhotoDoesNotExistsException()
        }

        val fileMd5 = Utils.getFileMd5(photoFile)
        val progressBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile)
        val multipartBody = MultipartBody.Part.createFormData("photos", photoFile.name, progressBody)

        return Pair(multipartBody, fileMd5)
    }
}