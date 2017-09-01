package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.MyMalfunctionRequestsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main.MyMalfunctionRequestsFragment
import dagger.Component

/**
 * Created by kirakishou on 8/21/2017.
 */

@PerFragment
@Component(modules = arrayOf(MyMalfunctionRequestsFragmentModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface MyMalfunctionRequestsFragmentComponent {
    fun inject(fragment: MyMalfunctionRequestsFragment)
}