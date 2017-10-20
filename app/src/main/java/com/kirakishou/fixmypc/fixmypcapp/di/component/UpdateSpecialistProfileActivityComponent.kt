package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateSpecialistProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateSpecialistProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.UpdateSpecialistProfileFragment
import dagger.Component

/**
 * Created by kirakishou on 10/10/2017.
 */

@PerActivity
@Component(modules = arrayOf(UpdateSpecialistProfileActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface UpdateSpecialistProfileActivityComponent {
    fun inject(activity: UpdateSpecialistProfileActivity)
    fun inject(fragment: UpdateSpecialistProfileFragment)
}