package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.api.FixmypcApi
import com.kirakishou.fixmypc.fixmypcapp.base.BaseServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class BackgroundServicePresenterImpl
    @Inject constructor(val mFixmypcApi: FixmypcApi,
                        val mEventBus: EventBus) : BaseServicePresenter<BackgroundServiceCallbacks>(), BackgroundServicePresenter {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        mEventBus.register(this)
    }

    override fun destroyPresenter() {
        mCompositeDisposable.dispose()
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
        when (message.type) {
            ServiceMessageType.SERVICE_MESSAGE_LOGIN -> testRequest(message.data as TestRequestParams)
            else -> Timber.e("Unsupported messageType: ${message.type}")
        }
    }

    override fun testRequest(testRequestParams: TestRequestParams) {
        mCompositeDisposable.add(mFixmypcApi.LoginRequest(this, testRequestParams))
    }
}