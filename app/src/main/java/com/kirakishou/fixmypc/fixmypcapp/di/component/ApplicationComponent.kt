package com.kirakishou.fixmypc.fixmypcapp.di.component

import android.content.Context
import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.di.module.ApplicationModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.AppSharedPreferences
import dagger.Component
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by kirakishou on 7/20/2017.
 */

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun exposeContext(): Context
    fun exposeSharedPreferences(): SharedPreferences
    fun exposeRetrofit(): Retrofit
    fun exposeEventBus(): EventBus
    fun exposeAppSharedPreferences(): AppSharedPreferences
    fun exposeAppSettings(): AppSettings
}