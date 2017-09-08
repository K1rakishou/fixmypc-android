package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ApiException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
open class ClientNewMalfunctionActivityPresenterImpl
@Inject constructor(protected val mApiClient: ApiClient) : BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()
    private val uploadProgressUpdateSubject = PublishSubject.create<ProgressUpdate>()

    fun initPresenter() {
        /*mCompositeDisposable += uploadProgressUpdateSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ progressUpdate ->
                    when (progressUpdate.type) {
                        ProgressUpdateType.Chunk -> callbacks.onProgressDialogUpdate((progressUpdate as ProgressUpdateChunk).progress)
                        ProgressUpdateType.FileUploaded -> callbacks.onFileUploaded()
                        ProgressUpdateType.Reset -> callbacks.onResetProgressDialog()
                    }
                }, { error ->
                    callbacks.onFileUploadingError(error)
                }, {
                    callbacks.onAllFilesUploaded()
                })*/

        Timber.d("ClientNewMalfunctionPresenterImpl.initPresenter()")
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

    fun sendMalfunctionRequestToServer(damageClaimInfo: DamageClaimInfo) {
        //callbacks.onInitProgressDialog(damageClaimInfo.damageClaimPhotos.size)

        mCompositeDisposable += mApiClient.createMalfunctionRequest(damageClaimInfo, uploadProgressUpdateSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    private fun handleResponse(response: MalfunctionResponse) {
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
        }

        /*callbacks.onAllFilesUploaded()
        callbacks.onMalfunctionRequestSuccessfullyCreated()*/
    }

    private fun handleError(error: Throwable) {
        if (error !is ApiException) {
            Timber.e(error)
        }

        //callbacks.onAllFilesUploaded()

        when (error) {
            /*is ApiException -> {
                val remoteErrorCode = error.errorCode

                when (remoteErrorCode) {
                    //Client should check for these two. They should never happen unless the client is patched
                    ErrorCode.Remote.REC_NO_FILES_WERE_SELECTED_TO_UPLOAD,
                    ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED -> {
                        throw IllegalStateException("This should never happen")
                    }

                    ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> callbacks.onFileSizeExceeded()
                    ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> callbacks.onRequestSizeExceeded()
                    ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> callbacks.onAllFileServersAreNotWorking()
                    ErrorCode.Remote.REC_DATABASE_ERROR -> callbacks.onServerDatabaseError()

                    else -> throw IllegalStateException("Unknown error code remoteErrorCode = $remoteErrorCode")
                }
            }

            is TimeoutException,
            is UnknownHostException -> {
                callbacks.onCouldNotConnectToServer(error)
            }

            is FileSizeExceededException -> callbacks.onFileSizeExceeded()
            is PhotosAreNotSetException -> callbacks.onPhotosAreNotSet()
            is SelectedPhotoDoesNotExistsException -> callbacks.onSelectedPhotoDoesNotExists()
            is ResponseBodyIsEmpty -> callbacks.onResponseBodyIsEmpty()
            is DuplicateObservableException -> callbacks.onFileAlreadySelected()

            else -> callbacks.onUnknownError(error)*/
        }
    }
}