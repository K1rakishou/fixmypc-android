package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientMainActivityView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
open class ClientMainActivityPresenterImpl
@Inject constructor(protected val mEventBus: EventBus) : ClientMainActivityPresenter<ClientMainActivityView>() {

    override fun initPresenter() {
        Timber.d("ClientMainActivityPresenterImpl.initPresenter()")

        mEventBus.register(this)
    }

    override fun destroyPresenter() {
        mEventBus.unregister(this)

        Timber.d("ClientMainActivityPresenterImpl.destroyPresenter()")
    }

    override fun sendServiceMessage(message: ServiceMessage) {
        mEventBus.postSticky(message)
    }

    override fun sendMalfunctionRequestToServer(malfunctionApplicationInfo: MalfunctionApplicationInfo) {
        sendServiceMessage(ServiceMessage(ServiceMessageType.SERVICE_MESSAGE_SEND_MALFUNCTION_APPLICATION,
                malfunctionApplicationInfo))
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun onEventAnswer(answer: ServiceAnswer) {
        when (answer.type) {
            ServiceMessageType.SERVICE_MESSAGE_SEND_MALFUNCTION_APPLICATION -> onMalfunctionApplicationResponse(answer)
            ServiceMessageType.SERVICE_MESSAGE_FILE_UPLOAD -> onFileUploadUpdate(answer)
            else -> Timber.e("Unsupported answerType: ${answer.type}")
        }
    }

    private fun onFileUploadUpdate(answer: ServiceAnswer) {

    }

    private fun onMalfunctionApplicationResponse(answer: ServiceAnswer) {
        val response = answer.data as ServerResponse<MalfunctionResponse>

        when (response) {
            is ServerResponse.Success -> {
                val errorCode = response.value.errorCode

                if (errorCode != ErrorCode.Remote.REC_OK) {
                    throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
                }

                callbacks.onMalfunctionRequestSuccessfullyCreated()
            }

            is ServerResponse.LocalError -> {
                val localErrorCode = response.errorCode

                when (localErrorCode) {
                    ErrorCode.Local.LEC_FILE_SIZE_EXCEEDED -> callbacks.onFileSizeExceeded()

                    //Client should check for these three. These will ever happen unless the client is patched
                    ErrorCode.Local.LEC_MAI_PHOTOS_ARE_NOT_SET,
                    ErrorCode.Local.LEC_MAI_CATEGORY_IS_NOT_SET,
                    ErrorCode.Local.LEC_MAI_DESCRIPTION_IS_NOT_SET -> {
                        //wtf
                        throw IllegalStateException("This should've never happened, but you did it! You somehow managed to " +
                                "skip one of the malfunction request's info selection fragments")
                    }

                    //wtf^2
                    else -> throw IllegalStateException("This should never happen localErrorCode = $localErrorCode")
                }
            }

            is ServerResponse.ServerError -> {
                val remoteErrorCode = response.errorCode

                when (remoteErrorCode) {
                    //Client should check for these two. These will ever happen unless the client is patched
                    ErrorCode.Remote.REC_NO_FILES_WERE_SELECTED_TO_UPLOAD,
                    ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED  -> {
                        throw IllegalStateException("This should never happen")
                    }

                    ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> callbacks.onFileSizeExceeded()
                    ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> callbacks.onRequestSizeExceeded()
                    ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> callbacks.onAllFileServersAreNotWorking()

                    else -> throw IllegalStateException("This should never happen remoteErrorCode = $remoteErrorCode")
                }
            }

            is ServerResponse.UnknownError -> {
                if (response.error is TimeoutException || response.error is UnknownHostException) {
                    callbacks.onCouldNotConnectToServer(response.error)
                    return
                }

                callbacks.onUnknownError(response.error)
            }
        }
    }

    class FileUploadProgressUpdater : UploadProgress {

        override fun onPartWritten(percent: Float) {

        }

        override fun onFileDone() {

        }

    }

    interface UploadProgress {
        fun onPartWritten(percent: Float)
        fun onFileDone()
    }
}