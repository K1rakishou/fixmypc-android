package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.CouldNotUpdateSessionId
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ResponseBodyIsEmpty
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.UserInfoIsEmpty
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileAlreadySelectedException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.PhotosAreNotSetException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import com.kirakishou.fixmypc.fixmypcapp.util.Utils
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
import com.kirakishou.fixmypc.fixmypcapp.util.retrofit.ProgressRequestBody
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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

    override fun createMalfunctionRequest(malfunctionRequestInfo: MalfunctionRequestInfo,
                                          uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse> {

        return Single.just(malfunctionRequestInfo)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    sendMalfunctionRequest(it, malfunctionRequestInfo, uploadProgressCallback)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun sendMalfunctionRequest(requestInfo: MalfunctionRequestInfo, malfunctionRequestInfo: MalfunctionRequestInfo,
                                       uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse> {

        if (requestInfo.malfunctionPhotos.isEmpty()) {
            return Single.error<MalfunctionResponse>(PhotosAreNotSetException())
        }

        //notify the activity to show upload dialog
        uploadProgressCallback.get()?.onPrepareForUploading(malfunctionRequestInfo.malfunctionPhotos.size)

        val photoPaths = malfunctionRequestInfo.malfunctionPhotos
        val multipartBodyPartsList = arrayListOf<MultipartBody.Part>()
        val filesMd5 = hashSetOf<String>()

        //prepare photos
        for (photoPath in photoPaths) {
            val photoFile = File(photoPath)
            if (photoFile.length() > Constant.MAX_FILE_SIZE) {
                return Single.error<MalfunctionResponse>(FileSizeExceededException())
            }

            if (!photoFile.isFile || !photoFile.exists()) {
                return Single.error<MalfunctionResponse>(SelectedPhotoDoesNotExistsException())
            }

            val fileMd5 = Utils.getFileMd5(photoFile)
            if (filesMd5.contains(fileMd5)) {
                return Single.error<MalfunctionResponse>(FileAlreadySelectedException())
            }

            filesMd5.add(fileMd5)

            val progressRequestBody = ProgressRequestBody(photoFile, uploadProgressCallback)
            multipartBodyPartsList.add(MultipartBody.Part.createFormData("photos", photoFile.name, progressRequestBody))
        }

        //we need sessionId for this request
        if (!mAppSettings.userInfo.isPresent()) {
            //Should never happen. userInfo must be set on user successful log in
            return Single.error<MalfunctionResponse>(UserInfoIsEmpty())
        }

        val userInfo = mAppSettings.userInfo.get()
        val request = MalfunctionRequest(requestInfo.malfunctionCategory.ordinal, requestInfo.malfunctionDescription)

        //send request
        return mApiService.sendMalfunctionRequest(userInfo.sessionId, multipartBodyPartsList, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                //handle HttpException
                .onErrorResumeNext { error ->
                    return@onErrorResumeNext handleBadHttpStatus(error, userInfo, multipartBodyPartsList, uploadProgressCallback, request)
                }
    }

    private fun handleBadHttpStatus(error: Throwable, userInfo: UserInfo,
                                    multipartBodyPartsList: ArrayList<MultipartBody.Part>,
                                    uploadProgressCallback: WeakReference<FileUploadProgressUpdater>,
                                    request: MalfunctionRequest): Single<MalfunctionResponse> {

        if (error !is HttpException) {
            return Single.error<MalfunctionResponse>(error)
        }

        val malfunctionResponseFickle = mErrorBodyConverter.convert<MalfunctionResponse>(error, MalfunctionResponse::class.java)
        if (!malfunctionResponseFickle.isPresent()) {
            return Single.error<MalfunctionResponse>(ResponseBodyIsEmpty())
        }

        val malfunctionResponse = malfunctionResponseFickle.get()

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

                    uploadProgressCallback.get()?.onReset()
                    mAppSettings.userInfo.get().sessionId = loginResponse.sessionId

                    Timber.d("Re login success!")
                    return@flatMap mApiService.sendMalfunctionRequest(loginResponse.sessionId, multipartBodyPartsList,
                            request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                }
    }
}




































