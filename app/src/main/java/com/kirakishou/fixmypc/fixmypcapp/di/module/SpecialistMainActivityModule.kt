package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.SpecialistMainActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 9/3/2017.
 */

@Module
class SpecialistMainActivityModule(val mView: SpecialistMainActivityView) {

    @PerActivity
    @Provides
    fun provideView(): SpecialistMainActivityView {
        return mView
    }
}