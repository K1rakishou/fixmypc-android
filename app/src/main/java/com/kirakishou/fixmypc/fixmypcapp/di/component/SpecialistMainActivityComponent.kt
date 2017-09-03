package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import dagger.Component

/**
 * Created by kirakishou on 9/3/2017.
 */

@PerActivity
@Component(modules = arrayOf(SpecialistMainActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface SpecialistMainActivityComponent {
    fun inject(activity: SpecialistMainActivity)
}