package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.LoadingActivity
import dagger.Component

/**
 * Created by kirakishou on 7/20/2017.
 */

@PerActivity
@Component(modules = arrayOf(LoadingActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface LoadingActivityComponent {
    fun inject(activity: LoadingActivity)
}