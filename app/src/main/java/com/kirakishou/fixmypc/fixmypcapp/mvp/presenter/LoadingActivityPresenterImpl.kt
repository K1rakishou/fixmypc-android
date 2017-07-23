package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
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

    override fun onStart() {
        mEventBus.register(this)
    }

    override fun onStop() {
        mEventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun onEventAnswer(answer: ServiceAnswer) {
        when (answer.id) {
            Constant.EVENT_MESSAGE_TEST -> Timber.e("answer is ${answer.data as String}")
        }
    }

    override fun sendServiceMessage(message: ServiceMessage) {
        mEventBus.postSticky(message)
    }
}