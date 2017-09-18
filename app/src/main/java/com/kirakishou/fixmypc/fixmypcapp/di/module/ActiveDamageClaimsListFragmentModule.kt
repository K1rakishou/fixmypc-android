package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtilsImpl
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ActiveDamageClaimListFragmentViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class ActiveDamageClaimsListFragmentModule(val activity: SpecialistMainActivity) {

    @PerFragment
    @Provides
    fun provideNavigator(): SpecialistMainActivityNavigator {
        return SpecialistMainActivityNavigator(activity)
    }

    @PerFragment
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient,
                                mWifiUtils: WifiUtilsImpl,
                                mDamageClaimRepo: DamageClaimRepository,
                                schedulers: SchedulerProvider): ActiveDamageClaimListFragmentViewModelFactory {
        return ActiveDamageClaimListFragmentViewModelFactory(mApiClient, mWifiUtils, mDamageClaimRepo, schedulers)
    }
}