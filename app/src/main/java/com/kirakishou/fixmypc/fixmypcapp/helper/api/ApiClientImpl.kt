package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.helper.util.retrofit.ProgressRequestBody
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ImageType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.PhotosAreNotSelectedException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */

class ApiClientImpl
@Inject constructor(protected val mApiService: ApiService,
                    protected val mAppSettings: AppSettings,
                    protected val mGson: Gson) : ApiClient {

    override fun loginRequest(loginRequest: LoginRequest): Single<LoginResponse> {
        return mApiService.doLogin(loginRequest)
                .lift(OnApiErrorSingle<LoginResponse>(mGson))
                .subscribeOn(Schedulers.io())
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: BehaviorSubject<ProgressUpdate>): Single<MalfunctionResponse> {

        //create MultipartFile bodies, check if user has selected the same file twice
        val progressBodyListObservable = Observable.fromIterable(damageClaimInfo.damageClaimPhotos)
                .subscribeOn(Schedulers.io())
                .map { prepareRequest(it, uploadProgressUpdateSubject) }
                //it.second is md5 string of the selected file
                //FIXME: Fix ErrorOnDuplicate operator
                //.lift(ErrorOnDuplicate<Pair<MultipartBody.Part, String>, String> { it.second })
                //we don't need md5 anymore
                .map { it.first }
                .toList()
                .toObservable()
                .publish()
                .autoConnect(2)

        val requestObservable = Observable.just(damageClaimInfo)

        //send request
        val responseObservable = Observables.zip(progressBodyListObservable, requestObservable, { a, b -> Pair(a, b) })
                .flatMap { sendRequest(it) }
                .onErrorResumeNext(Function {
                    if (it is ApiException) {
                        return@Function Observable.just(MalfunctionResponse(it.errorCode))
                    }

                    return@Function Observable.error(it)
                })
                .publish()
                .autoConnect(3)

        //if there were no errors - do nothing
        val firstAttemptOkObservable = responseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_OK }
                .doOnNext { _ -> uploadProgressUpdateSubject.onComplete() }

        //if there were REC_SESSION_ID_EXPIRED error - try to re login
        val sessionIdObservable = responseObservable
                .filter { it.errorCode == ErrorCode.Remote.REC_SESSION_ID_EXPIRED }
                .flatMap { reLogin() }
                //update sessionId with the new one
                .doOnNext { mAppSettings.updateSessionId(it) }

        //if there were some other error - do nothing
        val firstAttemptErrorObservable = responseObservable
                .filter { it.errorCode != ErrorCode.Remote.REC_OK && it.errorCode != ErrorCode.Remote.REC_SESSION_ID_EXPIRED }
                .doOnNext { _ -> uploadProgressUpdateSubject.onComplete() }

        //re send the request
        val secondAttemptObservable = Observables.zip(sessionIdObservable, progressBodyListObservable, requestObservable, { a, b, c -> Triple(a, b, c) })
                .doOnNext { _ -> uploadProgressUpdateSubject.onNext(ProgressUpdate.ProgressUpdateReset()) }
                .flatMap { resendRequest(it) }
                .doOnNext { _ -> uploadProgressUpdateSubject.onComplete() }

        //now we have three different outcomes:
        //either request was accepted by the server on the first try and returned REC_OK,
        //either request was not accepted by the server for some reason and returned some error code
        //or request was not accepted by the server BECAUSE user's sessionId was removed.
        //We are merging all of these three possibilities and retrieving the first observable (and the only one, we should only have ONE observable at this point)
        return Observable.merge(firstAttemptOkObservable, firstAttemptErrorObservable, secondAttemptObservable)
                .single(MalfunctionResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
                .onErrorResumeNext { error ->
                    val response = when (error) {
                        is ApiException -> MalfunctionResponse(error.errorCode)
                        is TimeoutException -> MalfunctionResponse(ErrorCode.Remote.REC_TIMEOUT)
                        is UnknownHostException -> MalfunctionResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)
                        is FileSizeExceededException -> MalfunctionResponse(ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED)
                        is PhotosAreNotSelectedException -> MalfunctionResponse(ErrorCode.Remote.REC_NO_PHOTOS_WERE_SELECTED_TO_UPLOAD)
                        is SelectedPhotoDoesNotExistsException -> MalfunctionResponse(ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS)
                        is ResponseBodyIsEmpty -> MalfunctionResponse(ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY)
                        is DuplicateEntryException -> MalfunctionResponse(ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION)

                        else -> throw RuntimeException("Unknown exception")
                    }

                    return@onErrorResumeNext Single.just(response)
                }
    }

    override fun getDamageClaims(lat: Double, lon: Double, radius: Double, page: Long): Single<DamageClaimsResponse> {
        return mApiService.getDamageClaims(lat, lon, radius, page)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
    }

    private fun resendRequest(it: Triple<String, List<MultipartBody.Part>, DamageClaimInfo>): Observable<MalfunctionResponse> {
        Timber.e("resendRequest")
        val request = MalfunctionRequest(it.third.damageClaimCategory.ordinal, it.third.damageClaimDescription,
                it.third.damageClaimLocation.latitude, it.third.damageClaimLocation.longitude)

        return mApiService.sendMalfunctionRequest(it.first, it.second, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
    }

    private fun reLogin(): Observable<String> {
        Timber.e("reLogin")
        val userInfoFickle = mAppSettings.userInfo

        if (!userInfoFickle.isPresent()) {
            throw UserInfoIsEmpty()
        }

        val userInfo = userInfoFickle.get()
        return mApiService.doLogin(LoginRequest(userInfo.login, userInfo.password))
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
                .map {
                    if (it.errorCode != ErrorCode.Remote.REC_OK) {
                        throw CouldNotUpdateSessionId()
                    }

                    return@map it.sessionId
                }
    }

    private fun sendRequest(it: Pair<List<MultipartBody.Part>, DamageClaimInfo>): Observable<MalfunctionResponse> {
        Timber.e("sendRequest")
        val userInfoFickle = mAppSettings.userInfo

        if (!userInfoFickle.isPresent()) {
            throw UserInfoIsEmpty()
        }

        val sessionId = userInfoFickle.get().sessionId
        val request = MalfunctionRequest(it.second.damageClaimCategory.ordinal, it.second.damageClaimDescription,
                it.second.damageClaimLocation.latitude, it.second.damageClaimLocation.longitude)

        return mApiService.sendMalfunctionRequest(sessionId, it.first, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                .lift(OnApiErrorSingle(mGson))
                .toObservable()
    }

    private fun prepareRequest(photoPath: String, uploadProgressUpdateSubject: BehaviorSubject<ProgressUpdate>): Pair<MultipartBody.Part, String> {
        Timber.e("prepareRequest")

        val photoFile = File(photoPath)
        if (photoFile.length() > Constant.MAX_FILE_SIZE) {
            throw FileSizeExceededException()
        }

        if (!photoFile.isFile || !photoFile.exists()) {
            throw SelectedPhotoDoesNotExistsException()
        }

        val fileMd5 = Utils.getFileMd5(photoFile)
        val progressBody = ProgressRequestBody(photoFile, uploadProgressUpdateSubject)
        val multipartBody = MultipartBody.Part.createFormData("photos", photoFile.name, progressBody)

        return Pair(multipartBody, fileMd5)
    }
}




































