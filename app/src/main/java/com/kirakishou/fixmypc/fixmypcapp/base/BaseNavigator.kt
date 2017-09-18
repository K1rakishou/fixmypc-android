package com.kirakishou.fixmypc.fixmypcapp.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by kirakishou on 9/15/2017.
 */
open class BaseNavigator {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Fragment> createNewFragmentIfNotInStack(fragmentManager: FragmentManager, fragmentTag: String): T {
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)

        if (fragment == null) {
            fragment = T::class.java.getDeclaredConstructor().newInstance() as T
        }

        return fragment as T
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Fragment> createNewFragmentIfNotInStackWithParams(fragmentManager: FragmentManager, fragmentTag: String, params: Bundle): T {
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = T::class.java.getDeclaredConstructor().newInstance() as T
        }

        fragment.arguments = params
        return fragment as T
    }
}