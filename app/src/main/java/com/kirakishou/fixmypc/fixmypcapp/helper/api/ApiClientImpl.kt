package com.kirakishou.fixmypc.fixmypcapp.helper.api

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdateReset
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.helper.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.helper.util.retrofit.ProgressRequestBody
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ImageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.UserInfoIsEmpty
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
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
                .lift(OnApiErrorSingle(mGson))
                .subscribeOn(Schedulers.io())
    }

    override fun createMalfunctionRequest(damageClaimInfo: DamageClaimInfo,
                                          uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>): Single<MalfunctionResponse> {

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
                .doOnNext { _ -> uploadProgressUpdateSubject.onNext(ProgressUpdateReset()) }
                .flatMap { resendRequest(it) }
                .doOnNext { _ -> uploadProgressUpdateSubject.onComplete() }

        //now we have three different outcomes:
        //either request was accepted by the server on the first try and returned REC_OK,
        //either request was not accepted by the server for some reason and returned some error code
        //or request was not accepted by the server BECAUSE user's sessionId was removed.
        //We are merging all of these three possibilities and retrieving the first observable (and the only one, we should only have ONE observable at this point)
        return Observable.merge(firstAttemptOkObservable, firstAttemptErrorObservable, secondAttemptObservable)
                .single(MalfunctionResponse(ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR))
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

    private fun prepareRequest(photoPath: String, uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>): Pair<MultipartBody.Part, String> {
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




































