package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.ClientMainActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 8/21/2017.
 */

@Module
class ClientMainActivityModule(val mView: ClientMainActivityView) {

    @PerActivity
    @Provides
    fun provideView(): ClientMainActivityView {
        return mView
    }
}