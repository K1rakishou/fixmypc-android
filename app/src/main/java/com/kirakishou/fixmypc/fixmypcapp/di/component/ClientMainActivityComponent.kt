package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import dagger.Component

/**
 * Created by kirakishou on 8/21/2017.
 */

@PerActivity
@Component(modules = arrayOf(ClientMainActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ClientMainActivityComponent {
    fun inject(activity: ClientMainActivity)
}