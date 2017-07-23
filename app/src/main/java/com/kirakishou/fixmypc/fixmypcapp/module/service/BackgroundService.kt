package com.kirakishou.fixmypc.fixmypcapp.module.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerBackgroundServiceComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.BackgroundServiceModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenterImpl
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
class BackgroundService : Service(), BackgroundServiceCallbacks {

    @Inject
    lateinit var mPresenter: BackgroundServicePresenterImpl

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
        mPresenter.initPresenter()

        Timber.e("BackgroundService created")
    }

    override fun onDestroy() {
        Timber.e("BackgroundService destroyed")
        mPresenter.destroyPresenter()

        super.onDestroy()
    }

    override fun onUnknownError(error: Throwable) {
        Timber.e(error)
    }
}































