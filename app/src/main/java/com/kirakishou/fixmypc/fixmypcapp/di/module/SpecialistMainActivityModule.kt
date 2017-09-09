package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class SpecialistMainActivityModule {

    @PerActivity
    @Provides
    fun provideViewModelFactory(): SpecialistMainActivityViewModelFactory {
        return SpecialistMainActivityViewModelFactory()
    }
}