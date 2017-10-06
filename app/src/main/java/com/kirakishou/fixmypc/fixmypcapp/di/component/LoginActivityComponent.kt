package com.kirakishou.fixmypc.fixmypcapp.di.component

import com.kirakishou.fixmypc.fixmypcapp.di.module.LoginActivityModule
import com.kirakishou.fixmypc.fixmypcapp.di.scope.PerActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.LoginActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.login.LoginFragment
import dagger.Component

/**
 * Created by kirakishou on 10/6/2017.
 */

@PerActivity
@Component(modules = arrayOf(LoginActivityModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface LoginActivityComponent {
    fun inject(activity: LoginActivity)
    fun inject(fragment: LoginFragment)
}