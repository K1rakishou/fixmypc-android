package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientNewMalfunctionActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/27/2017.
 */

@Module
class ClientNewMalfunctionActivityModule(val mView: ClientNewMalfunctionActivityView) {

    @PerActivity
    @Provides
    fun provideView(): ClientNewMalfunctionActivityView {
        return mView
    }
}