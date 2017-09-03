package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveMalfunctionsListFragmentView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class ActiveMalfunctionsListFragmentModule(val mView: ActiveMalfunctionsListFragmentView) {

    @PerFragment
    @Provides
    fun provideView(): ActiveMalfunctionsListFragmentView {
        return mView
    }
}