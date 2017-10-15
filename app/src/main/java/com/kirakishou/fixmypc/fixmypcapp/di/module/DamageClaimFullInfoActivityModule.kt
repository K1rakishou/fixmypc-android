package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.DamageClaimFullInfoActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.DamageClaimFullInfoActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.DamageClaimFullInfoActivity
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 10/15/2017.
 */

@Module
class DamageClaimFullInfoActivityModule(val activity: DamageClaimFullInfoActivity) {

    @PerActivity
    @Provides
    fun provideViewModelFactory(apiClient: ApiClient, appSettings: AppSettings,
                                mSchedulers: SchedulerProvider): DamageClaimFullInfoActivityViewModelFactory {
        return DamageClaimFullInfoActivityViewModelFactory(apiClient, appSettings, mSchedulers)
    }
}