package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BasePresenter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
class LoadingActivityPresenterImpl
    @Inject constructor(val mEventBus: EventBus): BasePresenter<LoadingActivityView>(), LoadingActivityPresenter {

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEventAnswer(message: ServiceAnswer) {
        when (message.id) {
            Constant.EVENT_MESSAGE_TEST -> Timber.e("answer is ${message.data as String}")
        }
    }
}