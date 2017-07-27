package com.kirakishou.fixmypc.fixmypcapp.di.module

import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ChooseCategoryActivityView
import dagger.Module
import dagger.Provides

/**
 * Created by kirakishou on 7/27/2017.
 */

@Module
class ChooseCategoryActivityModule(val mView: ChooseCategoryActivityView) {

    @PerActivity
    @Provides
    fun provideView(): ChooseCategoryActivityView {
        return mView
    }
}