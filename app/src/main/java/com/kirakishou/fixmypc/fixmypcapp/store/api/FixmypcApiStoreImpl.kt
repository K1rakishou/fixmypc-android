package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.UserInfoIsEmpty
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.PhotosAreNotSetException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
import com.kirakishou.fixmypc.fixmypcapp.util.retrofit.ProgressRequestBody
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiStoreImpl
    @Inject constructor(protected val mApiService: ApiService,
                        protected val mErrorBodyConverter: ErrorBodyConverter,
                        protected val mAppSettings: AppSettings) : FixmypcApiStore {

    override fun loginRequest(loginRequest: LoginRequest): Single<LoginResponse> {
        return mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun sendMalfunctionRequest(malfunctionRequestInfo: MalfunctionRequestInfo,
                                        uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse> {

        uploadProgressCallback.get()?.onPrepareForUploading(malfunctionRequestInfo.malfunctionPhotos.size)
        val compositeDisposable = CompositeDisposable()

        return Single.just(malfunctionRequestInfo)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    send(it, malfunctionRequestInfo, compositeDisposable, uploadProgressCallback)
                }
                .doOnEvent {
                    _, _ -> compositeDisposable.clear()
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun send(requestInfo: MalfunctionRequestInfo, malfunctionRequestInfo: MalfunctionRequestInfo,
                     compositeDisposable: CompositeDisposable, uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse> {

        if (requestInfo.malfunctionPhotos.isEmpty()) {
            return Single.error<MalfunctionResponse>(PhotosAreNotSetException())
        }

        val photoPaths = malfunctionRequestInfo.malfunctionPhotos
        val multipartBodyPartsList = arrayListOf<MultipartBody.Part>()

        for (photoPath in photoPaths) {
            val photoFile = File(photoPath)
            if (photoFile.length() > Constant.MAX_FILE_SIZE) {
                return Single.error<MalfunctionResponse>(FileSizeExceededException())
            }

            if (!photoFile.isFile || !photoFile.exists()) {
                return Single.error<MalfunctionResponse>(SelectedPhotoDoesNotExistsException())
            }

            val progressRequestBody = ProgressRequestBody(photoFile)

            compositeDisposable += progressRequestBody.getProgressSubject()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ percent ->
                        uploadProgressCallback.get()?.onChunkWrite(percent.toInt())
                    }, { error ->
                        //should never happen, but if it somehow happens - just rethrow it
                        throw RuntimeException(error)
                    }, {
                        uploadProgressCallback.get()?.onFileUploaded()
                    })

            multipartBodyPartsList.add(MultipartBody.Part.createFormData("photos", photoFile.name, progressRequestBody))
        }

        if (!mAppSettings.userInfo.isPresent()) {
            //Should never happen. userInfo must be set on user successful log in
            return Single.error<MalfunctionResponse>(UserInfoIsEmpty())
        }

        val userInfo = mAppSettings.userInfo.get()
        val request = MalfunctionRequest(requestInfo.malfunctionCategory.ordinal, requestInfo.malfunctionDescription)

        return mApiService.sendMalfunctionRequest(userInfo.sessionId, multipartBodyPartsList, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                .onErrorResumeNext { error ->
                    return@onErrorResumeNext handleMalfunctionErrorResponse(error, userInfo, multipartBodyPartsList, request)
                }
    }

    private fun handleMalfunctionErrorResponse(error: Throwable, userInfo: UserInfo, multipartBodyPartsList: ArrayList<MultipartBody.Part>,
                                               request: MalfunctionRequest): Single<MalfunctionResponse> {
        if (error !is HttpException) {
            return Single.error<MalfunctionResponse>(error)
        }

        val malfunctionResponse = mErrorBodyConverter.convert<MalfunctionResponse>(error.response().errorBody()!!.string(), MalfunctionResponse::class.java)

        //if server returned REC_SESSION_ID_EXPIRED that means our session was removed from the cache and we need to re login
        if (malfunctionResponse.errorCode != ErrorCode.Remote.REC_SESSION_ID_EXPIRED) {
            Timber.d("errorCode is REC_SESSION_ID_EXPIRED. We need to update the session")
            return Single.just(malfunctionResponse)
        }

        //trying to re login
        return mApiService.doLogin(LoginRequest(userInfo.login, userInfo.password))
                .flatMap { loginResponse ->
                    if (loginResponse.errorCode != ErrorCode.Remote.REC_OK) {
                        Timber.e("Couldn't re login. errorCode: ${loginResponse.errorCode}")
                        return@flatMap Single.error<MalfunctionResponse>(CouldNotUpdateSessionId())
                    }

                    mAppSettings.userInfo.get().sessionId = loginResponse.sessionId

                    Timber.d("Re login success!")
                    return@flatMap mApiService.sendMalfunctionRequest(loginResponse.sessionId, multipartBodyPartsList,
                            request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                }
    }
}




































