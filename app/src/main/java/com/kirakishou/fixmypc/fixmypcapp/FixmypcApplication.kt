package com.kirakishou.fixmypc.fixmypcapp

import android.app.Application
import com.kirakishou.fixmypc.fixmypcapp.di.component.ApplicationComponent
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerApplicationComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ApplicationModule
import timber.log.Timber
import timber.log.Timber.DebugTree



/**
 * Created by kirakishou on 7/20/2017.
 */
class FixmypcApplication : Application() {
    companion object {
        @JvmStatic lateinit var applicationComponent: ApplicationComponent
        private val mBaseUrl = "http://kez1911.asuscomm.com:8080/"
        private val mDatabaseName = "fixmypc_db"
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this, mBaseUrl, mDatabaseName))
                .build()

        initTimber()
    }

    private fun initTimber() {
        Timber.plant(DebugTree())
    }
}