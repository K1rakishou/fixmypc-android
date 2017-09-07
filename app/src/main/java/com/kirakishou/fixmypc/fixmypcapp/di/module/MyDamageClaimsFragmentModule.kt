package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.MyDamageClaimsFragmentView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 8/21/2017.
 */

@Module
class MyDamageClaimsFragmentModule(val mView: MyDamageClaimsFragmentView) {

    @PerFragment
    @Provides
    fun provideView(): MyDamageClaimsFragmentView {
        return mView
    }
}