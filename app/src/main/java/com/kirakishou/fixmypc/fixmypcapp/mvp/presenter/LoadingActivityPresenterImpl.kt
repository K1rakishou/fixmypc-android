package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
open class LoadingActivityPresenterImpl
@Inject constructor(protected val mEventBus: EventBus) : LoadingActivityPresenter<LoadingActivityView>() {

    override fun initPresenter() {
        Timber.d("LoadingActivityPresenterImpl.initPresenter()")

        mEventBus.register(this)
    }

    override fun destroyPresenter() {
        mEventBus.unregister(this)

        Timber.d("LoadingActivityPresenterImpl.destroyPresenter()")
    }

    override fun sendServiceMessage(message: ServiceMessage) {
        mEventBus.postSticky(message)
    }

    override fun startLoggingIn(login: String, password: String) {
        sendServiceMessage(ServiceMessage(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                LoginRequest(login, password)))
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun onEventAnswer(answer: ServiceAnswer) {
        when (answer.type) {
            ServiceMessageType.SERVICE_MESSAGE_LOGIN -> onLoginEventResponse(answer)
            else -> Timber.e("Unsupported answerType: ${answer.type}")
        }
    }

    fun onLoginEventResponse(answer: ServiceAnswer) {
        val response = answer.data as ServerResponse<LoginResponse>

        when (response) {
            is ServerResponse.Success -> {
                val sessionId = response.value.sessionId
                val accountType = response.value.accountType
                val errorCode = response.value.errorCode

                if (errorCode != ErrorCode.Remote.REC_OK) {
                    throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
                }

                when (accountType) {
                    AccountType.Client -> {
                        callbacks.runClientMainActivity(sessionId, accountType)
                    }

                    AccountType.Specialist -> {
                        callbacks.runSpecialistMainActivity(sessionId, accountType)
                    }

                    //should never happen
                    else -> throw IllegalStateException("Server returned accountType ${accountType}")
                }
            }

            is ServerResponse.ServerError -> {
                val errCode = response.errorCode

                when (errCode) {
                    ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD,
                    ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR -> {
                        callbacks.runGuestMainActivity()
                    }

                    else -> throw IllegalStateException("This should never happen errCode = $errCode")
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
}