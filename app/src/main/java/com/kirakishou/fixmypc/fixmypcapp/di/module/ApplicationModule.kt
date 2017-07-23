package com.kirakishou.fixmypc.fixmypcapp.di.module

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
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
    fun provideGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder().serializeNulls().create()
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(15000, TimeUnit.SECONDS) //TODO: Don't forget to change this on release build
                .readTimeout(15000, TimeUnit.SECONDS)    //TODO: Don't forget to change this on release build
                .build()
    }

    @Singleton
    @Provides
    fun provideRxJavaCallAdapterFactory(): RxJavaCallAdapterFactory {
        return RxJavaCallAdapterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, converterFactory: GsonConverterFactory, adapterFactory: RxJavaCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build()
    }

    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return EventBus.builder().build()
    }
}










































