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
        val loginResponse = answer.data as ServerResponse<LoginResponse>

        when (loginResponse) {
            is ServerResponse.Success -> {
                val sessionId = loginResponse.value.sessionId
                val accountType = loginResponse.value.accountType
                val serverErrorCode = loginResponse.value.error

                if (serverErrorCode != ErrorCode.Remote.REC_OK) {
                    throw IllegalStateException("ServerResponse is Success but serverErrorCode is not SEC_OK: $serverErrorCode")
                }

                when (accountType) {
                    AccountType.Client -> {
                        callbacks.runClientMainActivity(sessionId, accountType)
                    }

                    AccountType.Specialist -> {
                        callbacks.runSpecialistMainActivity(sessionId, accountType)
                    }

                    //should never happen
                    AccountType.Guest -> throw IllegalStateException("Server returned accountType.Guest")
                }
            }

            is ServerResponse.ServerError -> {
                val errCode = loginResponse.errorCode

                when (errCode) {
                    ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD,
                    ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR -> {
                        callbacks.runGuestMainActivity()
                    }

                    else -> callbacks.onServerError(errCode)
                }
            }

            is ServerResponse.UnknownError -> {
                if (loginResponse.error is TimeoutException || loginResponse.error is UnknownHostException) {
                    callbacks.onCouldNotConnectToServer(loginResponse.error)
                    return
                }

                callbacks.onUnknownError(loginResponse.error)
            }
        }
    }
}