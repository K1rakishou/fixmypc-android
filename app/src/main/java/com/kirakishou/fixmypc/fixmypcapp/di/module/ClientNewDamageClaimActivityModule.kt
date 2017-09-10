package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/27/2017.
 */

@Module
class ClientNewDamageClaimActivityModule(val activity: ClientNewDamageClaimActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): ClientNewDamageClaimActivityNavigator {
        return ClientNewDamageClaimActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient): ClientNewMalfunctionActivityViewModelFactory {
        return ClientNewMalfunctionActivityViewModelFactory(mApiClient)
    }
}