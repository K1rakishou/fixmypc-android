package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoadingActivityViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/20/2017.
 */

@Module
class LoadingActivityModule {

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient, mAppSettings: AppSettings): LoadingActivityViewModelFactory {
        return LoadingActivityViewModelFactory(mApiClient, mAppSettings)
    }

}