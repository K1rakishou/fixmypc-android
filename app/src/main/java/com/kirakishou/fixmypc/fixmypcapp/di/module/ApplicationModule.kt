package com.kirakishou.fixmypc.fixmypcapp.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.manager.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.module.shared_preference.AppSharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStoreImpl
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverterImpl
import com.kirakishou.fixmypc.fixmypcapp.util.type_adapter.AccountTypeTypeAdapter
import com.kirakishou.fixmypc.fixmypcapp.util.type_adapter.ErrorCodeRemoteTypeAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by kirakishou on 7/20/2017.
 */

@Module
class ApplicationModule(private val mContext: Context,
                        private val mBaseUrl: String) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return mContext
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
                .registerTypeAdapter(AccountType::class.java, AccountTypeTypeAdapter<AccountType>())
                .registerTypeAdapter(ErrorCode.Remote::class.java, ErrorCodeRemoteTypeAdapter<ErrorCode.Remote>())
                .create()
    }

    @Singleton
    @Provides
    fun provideErrorBodyConverter(gson: Gson): ErrorBodyConverter {
        return ErrorBodyConverterImpl(gson)
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
    fun provideFixmypcApiStore(mApiService: ApiService, mErrorBodyConverter: ErrorBodyConverter, mAppSettings: AppSettings): FixmypcApiStore {
        return FixmypcApiStoreImpl(mApiService, mErrorBodyConverter, mAppSettings)
    }

    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return EventBus.builder().build()
    }

    @Singleton
    @Provides
    fun provideAppSharedPreferences(sharedPreferences: SharedPreferences): AppSharedPreferences {
        return AppSharedPreferences(sharedPreferences)
    }

    @Singleton
    @Provides
    fun providePermissionManager(): PermissionManager {
        return PermissionManager()
    }
}










































