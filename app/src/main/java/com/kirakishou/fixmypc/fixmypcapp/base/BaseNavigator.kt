package com.kirakishou.fixmypc.fixmypcapp.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by kirakishou on 9/15/2017.
 */
open class BaseNavigator {

    fun <T : Fragment> createNewFragmentIfNotInStack(fragmentManager: FragmentManager, fragmentTag: String, fragmentClass: Class<*>): T {
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = fragmentClass.getDeclaredConstructor().newInstance() as T
        }

        return fragment as T
    }
}