package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewMalfunctionActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivity
import dagger.Component

/**
 * Created by kirakishou on 7/27/2017.
 */

@PerActivity
@Component(modules = arrayOf(ClientNewMalfunctionActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ChooseCategoryActivityComponent {
    fun inject(activity: ClientNewMalfunctionActivity)
}