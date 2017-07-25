package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.preference.AccountInfoPreference
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
open class LoadingActivityPresenterImpl
    @Inject constructor(protected val mEventBus: EventBus): LoadingActivityPresenter<LoadingActivityView>() {

    override fun initPresenter() {
        mEventBus.register(this)
    }

    override fun destroyPresenter() {
        mEventBus.unregister(this)
    }

    override fun sendServiceMessage(message: ServiceMessage) {
        mEventBus.postSticky(message)
    }

    override fun startLoggingIn(accountInfoPrefs: AccountInfoPreference) {
        val login = accountInfoPrefs.login.get()
        val password = accountInfoPrefs.password.get()

        sendServiceMessage(ServiceMessage(ServiceMessageType.SERVICE_MESSAGE_LOGIN, LoginRequest(login, password)))
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun onEventAnswer(answer: ServiceAnswer) {
        when (answer.type) {
            ServiceMessageType.SERVICE_MESSAGE_LOGIN -> onLoginEventResponse(answer)
        }
    }

    private fun onLoginEventResponse(answer: ServiceAnswer) {
        val loginResponse = answer.data.get() as LoginResponse
        Timber.e("sessionId = ${loginResponse.sessionId}")
    }
}