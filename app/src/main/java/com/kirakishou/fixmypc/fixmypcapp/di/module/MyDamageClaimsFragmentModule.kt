package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.MyDamageClaimsFragmentViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 8/21/2017.
 */

@Module
class MyDamageClaimsFragmentModule {

    @PerFragment
    @Provides
    fun provideViewModelFactory(): MyDamageClaimsFragmentViewModelFactory {
        return MyDamageClaimsFragmentViewModelFactory()
    }

}