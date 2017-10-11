package com.kirakishou.fixmypc.fixmypcapp.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.newInstance
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.LoadingIndicatorFragment
import kotlin.reflect.KClass

/**
 * Created by kirakishou on 9/15/2017.
 */
open class BaseNavigator(activity: AppCompatActivity) {

    protected val fragmentManager = activity.supportFragmentManager

    fun popFragment() {
        val currentFragment = getVisibleFragment()
        if (currentFragment != null) {
            if (currentFragment !is LoadingIndicatorFragment) {
                fragmentManager.popBackStack()
            }
        }
    }

    fun getVisibleFragment(): Fragment? {
        val fragments = fragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isVisible)
                    return fragment
            }
        }

        return null
    }

    fun getFragmentByTag(tag: String): Fragment? {
        val fragments = fragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment != null && fragment.tag == tag)
                    return fragment
            }
        }

        return null
    }

    fun showLoadingIndicatorFragment() {
        val visibleFragment = getVisibleFragment() ?: return
        if (visibleFragment is LoadingIndicatorFragment) {
            return
        }

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(visibleFragment)

        val newFragment = LoadingIndicatorFragment()
        fragmentTransaction
                .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.LOADING_INDICATOR)
                .addToBackStack(null)

        fragmentTransaction.commit()
    }

    fun hideLoadingIndicatorFragment() {
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            if (visibleFragment is LoadingIndicatorFragment) {
                fragmentManager.popBackStack()
            }
        }
    }

    fun navigateToFragment(fragmentClass: KClass<*>, fragmentTag: String, bundle: Bundle? = null, fragmentFrameId: Int = R.id.fragment_frame) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            if (visibleFragment::class == fragmentClass) {
                //do nothing if we are already showing this fragment
                return
            }

            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(fragmentTag)
        if (fragmentInStack == null) {
            val newFragment = fragmentClass.newInstance<Fragment>()
            if (bundle != null) {
                newFragment.arguments = bundle
            }

            fragmentTransaction
                    .add(fragmentFrameId, newFragment, fragmentTag)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
        }

        fragmentTransaction.commit()
    }

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
