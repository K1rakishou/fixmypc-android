package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.login.LoginFragment

/**
 * Created by kirakishou on 10/6/2017.
 */
class LoginActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun popFragment() {
        fragmentManager.popBackStack()
    }

    fun navigateToLoginFragment() {
        navigateToFragment(LoginFragment::class, Constant.FragmentTags.LOGIN_FRAGMENT)
    }
}