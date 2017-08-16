package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ImageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.PhotosAreNotSetException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
import com.kirakishou.fixmypc.fixmypcapp.util.retrofit.ProgressRequestBody
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiStoreImpl
    @Inject constructor(val mApiService: ApiService) : FixmypcApiStore {

    override fun loginRequest(loginRequest: LoginRequest): Single<LoginResponse> {
        return mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun sendMalfunctionRequest(malfunctionApplicationInfo: MalfunctionApplicationInfo,
                                        uploadProgressCallback: WeakReference<FileUploadProgressUpdater>): Single<MalfunctionResponse> {

        uploadProgressCallback.get()?.init(malfunctionApplicationInfo.malfunctionPhotos.size)

        return Single.just(malfunctionApplicationInfo)
                .subscribeOn(Schedulers.io())
                .flatMap { requestInfo ->
                    if (requestInfo.malfunctionPhotos.isEmpty()) {
                        return@flatMap Single.error<MalfunctionResponse>(PhotosAreNotSetException())
                    }

                    val photoPaths = malfunctionApplicationInfo.malfunctionPhotos
                    val multipartBodyPartsList = arrayListOf<MultipartBody.Part>()

                    for (photoPath in photoPaths) {
                        val photoFile = File(photoPath)
                        if (photoFile.length() > Constant.MAX_FILE_SIZE) {
                            return@flatMap Single.error<MalfunctionResponse>(FileSizeExceededException())
                        }

                        if (!photoFile.isFile || !photoFile.exists() || photoFile.isDirectory) {
                            return@flatMap Single.error<MalfunctionResponse>(SelectedPhotoDoesNotExistsException())
                        }

                        val progressRequestBody = ProgressRequestBody(photoFile)

                        //Dunno what to do with this disposable
                        //In theory, this should not leak anything, since this class is a singleton
                        progressRequestBody.getProgressSubject()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ percent ->
                                    uploadProgressCallback.get()?.onPartWrite(percent.toInt())
                                }, { error ->
                                    //should never happen
                                    Timber.e(error)
                                }, {
                                    uploadProgressCallback.get()?.onFileDone()
                                })

                        multipartBodyPartsList.add(MultipartBody.Part.createFormData("photos", photoFile.name, progressRequestBody))
                    }

                    val request = MalfunctionRequest(requestInfo.malfunctionCategory.ordinal, requestInfo.malfunctionDescription)
                    return@flatMap mApiService.sendMalfunctionRequest(multipartBodyPartsList, request, ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }
}