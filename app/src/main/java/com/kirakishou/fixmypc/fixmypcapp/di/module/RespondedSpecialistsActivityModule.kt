package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.RespondedSpecialistsActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.RespondedSpecialistsActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.RespondedSpecialistsActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 10/2/2017.
 */

@Module
class RespondedSpecialistsActivityModule(val activity: RespondedSpecialistsActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): RespondedSpecialistsActivityNavigator {
        return RespondedSpecialistsActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(mApiClient: ApiClient,
                                schedulers: SchedulerProvider): RespondedSpecialistsActivityViewModelFactory {
        return RespondedSpecialistsActivityViewModelFactory(mApiClient, schedulers)
    }

}