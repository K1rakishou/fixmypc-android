package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateClientProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateSpecialistProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import dagger.Component

/**
 * Created by kirakishou on 10/20/2017.
 */

@PerActivity
@Component(modules = arrayOf(UpdateClientProfileActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface UpdateClientProfileActivityComponent {
    fun inject(activity: UpdateClientProfileActivity)
}