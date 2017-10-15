package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.DamageClaimFullInfoActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.DamageClaimFullInfoActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.DamageClaimFullInfoFragment
import dagger.Component

/**
 * Created by kirakishou on 10/15/2017.
 */

@PerActivity
@Component(modules = arrayOf(DamageClaimFullInfoActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface DamageClaimFullInfoActivityComponent {
    fun inject(activity: DamageClaimFullInfoActivity)
    fun inject(fragment: DamageClaimFullInfoFragment)
}