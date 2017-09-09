package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ActiveMalfunctionsListFragmentViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class ActiveDamageClaimsListFragmentModule {

    @PerFragment
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient): ActiveMalfunctionsListFragmentViewModelFactory {
        return ActiveMalfunctionsListFragmentViewModelFactory(mApiClient)
    }
}