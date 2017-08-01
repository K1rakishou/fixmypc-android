package com.kirakishou.fixmypc.fixmypcapp.di.component

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.di.module.ApplicationModule
import com.kirakishou.fixmypc.fixmypcapp.manager.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.module.shared_preference.AppSharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
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
    fun exposeGson(): Gson
    fun exposeErrorBodyConverter(): ErrorBodyConverter
    fun exposeRetrofit(): Retrofit
    fun exposeEventBus(): EventBus
    fun exposeAppSharedPreferences(): AppSharedPreferences
    fun exposeAppSettings(): AppSettings
    fun exposePermissionManager(): PermissionManager
}