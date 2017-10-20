package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateClientProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateClientProfileActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 10/20/2017.
 */

@Module
class UpdateClientProfileActivityModule(val activity: UpdateClientProfileActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): UpdateClientProfileActivityNavigator {
        return UpdateClientProfileActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(apiClient: ApiClient,
                                schedulers: SchedulerProvider): UpdateClientProfileActivityViewModelFactory {
        return UpdateClientProfileActivityViewModelFactory(apiClient, schedulers)
    }
}