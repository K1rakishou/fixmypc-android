package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ChangeSpecialistProfileFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.DamageClaimFullInfoFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.SpecialistProfileFragment
import dagger.Component

/**
 * Created by kirakishou on 9/3/2017.
 */

@PerActivity
@Component(modules = arrayOf(SpecialistMainActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface SpecialistMainActivityComponent {
    fun inject(activity: SpecialistMainActivity)
    fun inject(fragment: ActiveDamageClaimsListFragment)
    fun inject(fragment: DamageClaimFullInfoFragment)
    fun inject(fragment: SpecialistProfileFragment)
    fun inject(fragment: ChangeSpecialistProfileFragment)
}