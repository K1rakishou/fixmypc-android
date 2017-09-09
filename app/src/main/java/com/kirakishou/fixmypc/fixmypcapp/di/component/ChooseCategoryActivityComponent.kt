package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimCategoryFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimDescriptionFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimLocationFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimPhotosFragment
import dagger.Component

/**
 * Created by kirakishou on 7/27/2017.
 */

@PerActivity
@Component(modules = arrayOf(ClientNewDamageClaimActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ChooseCategoryActivityComponent {
    fun inject(activity: ClientNewDamageClaimActivity)
    fun inject(fragment: DamageClaimCategoryFragment)
    fun inject(fragment: DamageClaimDescriptionFragment)
    fun inject(fragment: DamageClaimPhotosFragment)
    fun inject(fragment: DamageClaimLocationFragment)
}