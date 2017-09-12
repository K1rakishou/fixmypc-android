package com.kirakishou.fixmypc.fixmypcapp.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClientImpl
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.AccountTypeTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.ErrorCodeRemoteTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.helper.util.gson.LatLngTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by kirakishou on 7/20/2017.
 */

@Module
class ApplicationModule(private val mApplication: Application,
                        private val mBaseUrl: String) {

    @Singleton
    @Provides
    fun provideApplication(): Application {
        return mApplication
    }

    @Singleton
    @Provides
    fun provideContext(): Context {
        return mApplication.applicationContext
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constant.SHARED_PREFS_PREFIX, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(AccountType::class.java, AccountTypeTypeAdapter())
                .registerTypeAdapter(ErrorCode.Remote::class.java, ErrorCodeRemoteTypeAdapter())
                .registerTypeAdapter(LatLng::class.java, LatLngTypeAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(15000, TimeUnit.SECONDS) //TODO: Don't forget to change this on release build
                .writeTimeout(15000, TimeUnit.SECONDS)  //TODO: Don't forget to change this on release build
                .readTimeout(15000, TimeUnit.SECONDS)    //TODO: Don't forget to change this on release build
                //.addInterceptor(loggingInterceptor)
                .build()
    }

    @Singleton
    @Provides
    fun provideRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, converterFactory: GsonConverterFactory, adapterFactory: RxJava2CallAdapterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppSettings(): AppSettings {
        return AppSettings()
    }

    @Singleton
    @Provides
    fun provideFixmypcApiStore(mApiService: ApiService, mAppSettings: AppSettings, mGson: Gson): ApiClient {
        return ApiClientImpl(mApiService, mAppSettings, mGson)
    }

    @Singleton
    @Provides
    fun provideAppSharedPreferences(sharedPreferences: SharedPreferences): AppSharedPreference {
        return AppSharedPreference(sharedPreferences)
    }

    @Singleton
    @Provides
    fun providePermissionManager(): PermissionManager {
        return PermissionManager()
    }

    @Singleton
    @Provides
    fun provideRefWatcher(application: Application): RefWatcher {
        return LeakCanary.install(application)
    }

    @Singleton
    @Provides
    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader(context, mBaseUrl)
    }
}










































