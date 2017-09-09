package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/27/2017.
 */

@Module
class ClientNewDamageClaimActivityModule {

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient): ClientNewMalfunctionActivityViewModelFactory {
        return ClientNewMalfunctionActivityViewModelFactory(mApiClient)
    }
}