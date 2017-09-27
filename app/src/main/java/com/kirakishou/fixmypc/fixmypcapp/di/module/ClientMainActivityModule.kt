package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientMainActivityNavigator
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 8/21/2017.
 */

@Module
class ClientMainActivityModule(val activity: ClientMainActivity) {

    @PerActivity
    @Provides
    fun provideNavigator(): ClientMainActivityNavigator {
        return ClientMainActivityNavigator(activity)
    }

    @PerActivity
    @Provides
    fun provideViewModelFactory(): ClientMainActivityViewModelFactory {
        return ClientMainActivityViewModelFactory()
    }
}