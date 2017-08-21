package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.LoadingActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/20/2017.
 */

@Module
class LoadingActivityModule(val mView: LoadingActivityView) {

    @PerActivity
    @Provides
    fun provideView(): LoadingActivityView {
        return mView
    }
}