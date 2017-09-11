package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
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
    fun provideViewModelFactory(): SpecialistMainActivityViewModelFactory {
        return SpecialistMainActivityViewModelFactory()
    }
}