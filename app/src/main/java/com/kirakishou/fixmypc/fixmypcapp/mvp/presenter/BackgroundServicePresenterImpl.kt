package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.api.RequestFactory
import com.kirakishou.fixmypc.fixmypcapp.base.BaseServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class BackgroundServicePresenterImpl
    @Inject constructor(val mRequestFactory: RequestFactory,
                        val mEventBus: EventBus) : BaseServicePresenter<BackgroundServiceCallbacks>(), BackgroundServicePresenter {

    override fun onInitPresenter() {
        mEventBus.register(this)
    }

    override fun onTeardownPresenter() {
        mEventBus.unregister(this)
    }

    override fun sendClientAnswer(answer: ServiceAnswer) {
        mEventBus.postSticky(answer)
    }

    override fun returnAnswer(answer: ServiceAnswer) {
        sendClientAnswer(answer)
    }

    override fun onUnknownError(error: Throwable) {
        callbacks.onUnknownError(error)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    override fun onClientMessage(message: ServiceMessage) {
        when (message.id) {
            Constant.EVENT_MESSAGE_TEST -> testRequest(message.data as TestRequestParams)
        }
    }

    override fun testRequest(testRequestParams: TestRequestParams) {
        mRequestFactory.LoginRequest(this, testRequestParams)
    }
}