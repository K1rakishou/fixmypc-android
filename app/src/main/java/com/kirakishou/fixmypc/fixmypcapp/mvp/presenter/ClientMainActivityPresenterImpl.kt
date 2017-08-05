package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientMainActivityView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
open class ClientMainActivityPresenterImpl
    @Inject constructor(protected val mEventBus: EventBus): ClientMainActivityPresenter<ClientMainActivityView>() {

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
            else -> Timber.e("Unsupported answerType: ${answer.type}")
        }
    }

    private fun onMalfunctionApplicationResponse(answer: ServiceAnswer) {

    }
}