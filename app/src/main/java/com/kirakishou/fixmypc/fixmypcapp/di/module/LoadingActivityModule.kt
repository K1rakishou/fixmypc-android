package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.LoadingActivityViewModelImpl
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/20/2017.
 */

@Module
class LoadingActivityModule {

    @PerActivity
    @Provides
    fun provideLoadingActivityViewModel(mApiClient: ApiClient, mAppSettings: AppSettings): LoadingActivityViewModelImpl {
        return LoadingActivityViewModelImpl(mApiClient, mAppSettings)
    }
}