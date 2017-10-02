package com.kirakishou.fixmypc.fixmypcapp.di.component

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.di.module.ApplicationModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.database.MyDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.squareup.leakcanary.RefWatcher
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by kirakishou on 7/20/2017.
 */

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun exposeContext(): Context
    fun exposeDatabase(): MyDatabase
    fun exposeSharedPreferences(): SharedPreferences
    fun exposeGson(): Gson
    fun exposeRetrofit(): Retrofit
    fun exposeApiService(): ApiService
    fun exposeApiClient(): ApiClient
    fun exposeAppSharedPreferences(): AppSharedPreference
    fun exposeAppSettings(): AppSettings
    fun exposePermissionManager(): PermissionManager
    fun exposeRefWatcher(): RefWatcher
    fun exposeImageLoader(): ImageLoader
    fun exposeMapperManager(): MapperManager
    fun exposeDamageClaimRepository(): DamageClaimRepository
    fun exposeSchedulerProvider(): SchedulerProvider
    fun exposeWifiUtils(): WifiUtils
}