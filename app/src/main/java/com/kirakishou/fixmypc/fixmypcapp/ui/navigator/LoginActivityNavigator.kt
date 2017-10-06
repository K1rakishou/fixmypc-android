package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
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
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.LOGIN_FRAGMENT)
        if (fragmentInStack == null) {
            val newFragment = LoginFragment()

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.LOGIN_FRAGMENT)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
    }
}