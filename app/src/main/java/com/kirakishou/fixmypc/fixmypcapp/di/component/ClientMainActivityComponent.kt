package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientMyDamageClaimsFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientProfileFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.ClientActiveDamageClaimsList
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.ClientInactiveDamageClaimsList
import dagger.Component

/**
 * Created by kirakishou on 8/21/2017.
 */

@PerActivity
@Component(modules = arrayOf(ClientMainActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface ClientMainActivityComponent {
    fun inject(activity: ClientMainActivity)
    fun inject(fragment: ClientProfileFragment)
    fun inject(fragment: ClientMyDamageClaimsFragment)
    fun inject(fragment: ClientActiveDamageClaimsList)
    fun inject(fragment: ClientInactiveDamageClaimsList)
}