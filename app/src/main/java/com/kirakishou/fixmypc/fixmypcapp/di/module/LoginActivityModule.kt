package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoginActivityViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 10/6/2017.
 */

@Module
class LoginActivityModule {

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient, mAppSettings: AppSettings,
                                mSchedulers: SchedulerProvider): LoginActivityViewModelFactory {
        return LoginActivityViewModelFactory(mApiClient, mAppSettings, mSchedulers)
    }
}