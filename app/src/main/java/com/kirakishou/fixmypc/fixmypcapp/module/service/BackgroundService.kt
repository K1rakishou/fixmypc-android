package com.kirakishou.fixmypc.fixmypcapp.module.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerBackgroundServiceComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.BackgroundServiceModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
class BackgroundService : Service(), BackgroundServiceCallbacks {

    @Inject
    lateinit var mPresenter: BackgroundServicePresenter

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    fun resolveDaggerDependency() {
        DaggerBackgroundServiceComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .backgroundServiceModule(BackgroundServiceModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate() {
        super.onCreate()

        resolveDaggerDependency()
        EventBus.getDefault().register(this)

        Timber.e("BackgroundService created")
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        Timber.e("BackgroundService destroyed")

        super.onDestroy()
    }

    override fun sendClientAnswer(answer: ServiceAnswer) {
        EventBus.getDefault().postSticky(answer)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onClientMessage(message: ServiceMessage) {
        when (message.id) {
            Constant.EVENT_MESSAGE_TEST -> mPresenter.testRequest(message.data as TestRequestParams)
        }
    }

    override fun onUnknownError(error: Throwable) {
        Timber.e(error)
    }
}































