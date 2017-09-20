package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtilsImpl
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class SpecialistMainActivityModule(val activity: SpecialistMainActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): SpecialistMainActivityNavigator {
        return SpecialistMainActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient,
                                mWifiUtils: WifiUtilsImpl,
                                mDamageClaimRepo: DamageClaimRepository,
                                schedulers: SchedulerProvider): SpecialistMainActivityViewModelFactory {
        return SpecialistMainActivityViewModelFactory(mApiClient, mWifiUtils, mDamageClaimRepo, schedulers)
    }
}