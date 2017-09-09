package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.MyDamageClaimsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main.MyDamageClaimsFragment
import dagger.Component

/**
 * Created by kirakishou on 8/21/2017.
 */

@PerFragment
@Component(modules = arrayOf(MyDamageClaimsFragmentModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface MyDamageClaimsFragmentComponent {
    fun inject(fragment: MyDamageClaimsFragment)
}