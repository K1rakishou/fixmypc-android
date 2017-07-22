package com.kirakishou.fixmypc.fixmypcapp

import android.app.Application
import android.content.Intent
import com.kirakishou.fixmypc.fixmypcapp.di.component.ApplicationComponent
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerApplicationComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ApplicationModule
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundService
import timber.log.Timber
import timber.log.Timber.DebugTree



/**
 * Created by kirakishou on 7/20/2017.
 */
class FixmypcApplication : Application() {
    companion object {
        @JvmStatic lateinit var applicationComponent: ApplicationComponent
        private val mBaseUrl = "http://kez1911.asuscomm.com:8080/"
    }

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, BackgroundService::class.java))

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this, mBaseUrl))
                .build()

        Timber.plant(DebugTree())
        Timber.tag("FixmypcApplication")
    }
}