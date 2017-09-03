package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveMalfunctionsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveMalfunctionsListFragment
import dagger.Component

/**
 * Created by kirakishou on 9/3/2017.
 */

@PerFragment
@Component(modules = arrayOf(ActiveMalfunctionsListFragmentModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ActiveMalfunctionsListFragmentComponent {
    fun inject(fragment: ActiveMalfunctionsListFragment)
}