package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ChooseCategoryActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.module.activity.ClientNewMalfunctionActivity
import dagger.Component

/**
 * Created by kirakishou on 7/27/2017.
 */

@PerActivity
@Component(modules = arrayOf(ChooseCategoryActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ChooseCategoryActivityComponent {
    fun inject(activity: ClientNewMalfunctionActivity)
}