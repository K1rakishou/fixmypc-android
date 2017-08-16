package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.PhotosAreNotSetException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientMainActivityView
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.progress_updater.FileUploadProgressUpdater
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import retrofit2.HttpException
import timber.log.Timber
import java.lang.ref.WeakReference
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
open class ClientMainActivityPresenterImpl
@Inject constructor(protected val mFixmypcApiStore: FixmypcApiStore,
                    protected val errorBodyConverter: ErrorBodyConverter) : ClientMainActivityPresenter<ClientMainActivityView>(), FileUploadProgressUpdater {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("ClientMainActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("ClientMainActivityPresenterImpl.destroyPresenter()")
    }

    override fun sendMalfunctionRequestToServer(malfunctionApplicationInfo: MalfunctionApplicationInfo) {
        mCompositeDisposable += mFixmypcApiStore.sendMalfunctionRequest(malfunctionApplicationInfo, WeakReference(this))
                .subscribe({ response ->
                    val errorCode = response.errorCode

                    if (errorCode != ErrorCode.Remote.REC_OK) {
                        throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
                    }

                    callbacks.onMalfunctionRequestSuccessfullyCreated()

                }, { error ->
                    handleResponse(error)
                })
    }

    private fun handleResponse(error: Throwable) {
        Timber.e(error)

        when (error) {
            is HttpException -> {
                val response = errorBodyConverter.convert<MalfunctionResponse>(error.response().errorBody()!!.string(), MalfunctionResponse::class.java)
                val remoteErrorCode = response.errorCode

                when (remoteErrorCode) {
                    //Client should check for these two. These will ever happen unless the client is patched
                    ErrorCode.Remote.REC_NO_FILES_WERE_SELECTED_TO_UPLOAD,
                    ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED -> {
                        throw IllegalStateException("This should never happen")
                    }

                    ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> callbacks.onFileSizeExceeded()
                    ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> callbacks.onRequestSizeExceeded()
                    ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> callbacks.onAllFileServersAreNotWorking()

                    else -> throw IllegalStateException("This should never happen remoteErrorCode = $remoteErrorCode")
                }
            }

            is TimeoutException,
            is UnknownHostException -> {
                callbacks.onCouldNotConnectToServer(error)
            }

            is FileSizeExceededException -> callbacks.onFileSizeExceeded()
            is PhotosAreNotSetException -> callbacks.onPhotosAreNotSet()
            is SelectedPhotoDoesNotExistsException -> callbacks.onSelectedPhotoDoesNotExists()
            else -> callbacks.onUnknownError(error)
        }
    }

    override fun onPartWrite(percent: Float) {
        Timber.d("percent: $percent")
    }

    override fun onFileDone() {
        Timber.d("file done")
    }
}