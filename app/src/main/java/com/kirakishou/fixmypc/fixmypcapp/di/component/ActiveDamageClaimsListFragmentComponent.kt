package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ActiveDamageClaimsListFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.DamageClaimFullInfoFragment
import dagger.Component

/**
 * Created by kirakishou on 9/3/2017.
 */

@PerFragment
@Component(modules = arrayOf(ActiveDamageClaimsListFragmentModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ActiveDamageClaimsListFragmentComponent {
    fun inject(fragment: ActiveDamageClaimsListFragment)
    fun inject(fragment: DamageClaimFullInfoFragment)
}