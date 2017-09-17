package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtilsImpl
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
    fun provideViewModelFactory(mApiClient: ApiClient,
                                mWifiUtils: WifiUtilsImpl,
                                mDamageClaimRepo: DamageClaimRepository,
                                schedulers: SchedulerProvider): ActiveMalfunctionsListFragmentViewModelFactory {
        return ActiveMalfunctionsListFragmentViewModelFactory(mApiClient, mWifiUtils, mDamageClaimRepo, schedulers)
    }
}