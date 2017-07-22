package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.BackgroundServiceModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerService
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundService
import dagger.Component

/**
 * Created by kirakishou on 7/22/2017.
 */

@PerService
@Component(modules = arrayOf(BackgroundServiceModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface BackgroundServiceComponent {
    fun inject(service: BackgroundService)
}