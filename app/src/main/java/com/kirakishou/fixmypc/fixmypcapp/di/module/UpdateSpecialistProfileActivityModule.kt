package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateSpecialistProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateSpecialistProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateSpecialistProfileActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 10/10/2017.
 */

@Module
class UpdateSpecialistProfileActivityModule(val activity: UpdateSpecialistProfileActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): UpdateSpecialistProfileActivityNavigator {
        return UpdateSpecialistProfileActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient,
                                schedulers: SchedulerProvider): UpdateSpecialistProfileActivityViewModelFactory {
        return UpdateSpecialistProfileActivityViewModelFactory(mApiClient, schedulers)
    }
}