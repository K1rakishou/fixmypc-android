package com.kirakishou.fixmypc.fixmypcapp

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.InMemorySharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClientImpl
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepositoryImpl
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.database.MyDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.NormalSchedulers
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.AccountTypeTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.ErrorCodeRemoteTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.LatLngTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtilsImpl
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import okhttp3.OkHttpClient
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by kirakishou on 9/17/2017.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, minSdk = 19, maxSdk = 26)
open class BaseRobolectricTestCase {
    private val mBaseUrl = "http://kez1911.asuscomm.com:8080/"

    fun provideContext(): Context {
        return RuntimeEnvironment.application
    }

    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(AccountType::class.java, AccountTypeTypeAdapter())
                .registerTypeAdapter(ErrorCode.Remote::class.java, ErrorCodeRemoteTypeAdapter())
                .registerTypeAdapter(LatLng::class.java, LatLngTypeAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create()
    }

    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(provideGson())
    }

    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
    }

    fun provideRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(provideGsonConverterFactory())
                .addCallAdapterFactory(provideRxJavaCallAdapterFactory())
                .client(provideOkHttpClient())
                .build()
    }

    fun provideApiService(): ApiService {
        return provideRetrofit().create(ApiService::class.java)
    }

    fun provideAppSettings(): AppSettings {
        return AppSettings()
    }

    fun provideMapperManager(): MapperManager {
        return MapperManager()
    }

    fun provideSchedulers(): SchedulerProvider {
        return NormalSchedulers()
    }

    fun provideDatabase(): MyDatabase {
        return Room.inMemoryDatabaseBuilder(provideContext(), MyDatabase::class.java).build()
    }

    fun provideDamageClaimRepository(): DamageClaimRepository {
        return DamageClaimRepositoryImpl(provideDatabase(), provideMapperManager(), provideSchedulers())
    }

    fun provideWifiUtils(): WifiUtils {
        return WifiUtilsImpl(provideContext())
    }

    fun provideApiClient(): ApiClient {
        return ApiClientImpl(provideApiService(), provideAppSettings(), provideGson())
    }

    fun provideSharedPreferences(): SharedPreferences {
        return InMemorySharedPreferences()
    }

    fun provideAppSharedPreferences(): AppSharedPreference {
        return AppSharedPreference(provideSharedPreferences())
    }

    fun provideImageLoader(): ImageLoader {
        return ImageLoader(provideContext(), provideWifiUtils(), mBaseUrl)
    }
}




























