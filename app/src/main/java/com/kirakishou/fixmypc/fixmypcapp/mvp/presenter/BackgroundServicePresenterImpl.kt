package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class BackgroundServicePresenterImpl
    @Inject constructor(protected val mFixmypcApiStore: FixmypcApiStore,
                        protected val mEventBus: EventBus) : BaseServicePresenter<BackgroundServiceCallbacks>(), BackgroundServicePresenter {

    override fun initPresenter() {
        mFixmypcApiStore.callbacks = Fickle.of(this)
        mEventBus.register(this)
    }

    override fun destroyPresenter() {
        mFixmypcApiStore.callbacks = Fickle.empty()
        mEventBus.unregister(this)
    }

    override fun returnAnswer(answer: ServiceAnswer) {
        mEventBus.postSticky(answer)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    override fun onClientMessage(message: ServiceMessage) {
        when (message.type) {
            ServiceMessageType.SERVICE_MESSAGE_LOGIN -> testRequest(message.data as LoginRequest)
            else -> Timber.e("Unsupported messageType: ${message.type}")
        }
    }

    override fun testRequest(loginRequest: LoginRequest) {
        mFixmypcApiStore.loginRequest(loginRequest, ServiceMessageType.SERVICE_MESSAGE_LOGIN)
    }
}