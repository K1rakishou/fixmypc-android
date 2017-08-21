package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ResponseBodyIsEmpty
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileAlreadySelectedException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.FileSizeExceededException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.PhotosAreNotSetException
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.malfunction_request.SelectedPhotoDoesNotExistsException
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientNewMalfunctionActivityView
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
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
open class ClientNewMalfunctionPresenterImpl
@Inject constructor(protected val mFixmypcApiStore: FixmypcApiStore,
                    protected val errorBodyConverter: ErrorBodyConverter) : ClientNewMalfunctionPresenter<ClientNewMalfunctionActivityView>(), FileUploadProgressUpdater {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("ClientNewMalfunctionPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("ClientNewMalfunctionPresenterImpl.destroyPresenter()")
    }

    override fun sendMalfunctionRequestToServer(malfunctionRequestInfo: MalfunctionRequestInfo) {
        mCompositeDisposable += mFixmypcApiStore.createMalfunctionRequest(malfunctionRequestInfo, WeakReference(this))
                .subscribe({ response ->
                    val errorCode = response.errorCode

                    if (errorCode != ErrorCode.Remote.REC_OK) {
                        throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
                    }

                    callbacks.onAllFilesUploaded()
                    callbacks.onMalfunctionRequestSuccessfullyCreated()

                }, { error ->
                    callbacks.onAllFilesUploaded()
                    handleError(error)
                })
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)

        when (error) {
            is HttpException -> {
                val responseFickle = errorBodyConverter.convert<MalfunctionResponse>(error, MalfunctionResponse::class.java)
                if (!responseFickle.isPresent()) {
                    callbacks.onResponseBodyIsEmpty()
                }

                val response = responseFickle.get()
                val remoteErrorCode = response.errorCode

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
            is FileAlreadySelectedException -> callbacks.onFileAlreadySelected()

            else -> callbacks.onUnknownError(error)
        }
    }

    override fun onPrepareForUploading(filesCount: Int) {
        callbacks.onInitProgressDialog(filesCount)
    }

    override fun onChunkWrite(progress: Int) {
        callbacks.onProgressDialogUpdate(progress)
    }

    override fun onFileUploaded() {
        callbacks.onFileUploaded()
    }

    override fun onReset() {
        callbacks.resetProgressDialog()
    }

    override fun onError(e: Throwable) {
        callbacks.onFileUploadingError(e)
    }
}