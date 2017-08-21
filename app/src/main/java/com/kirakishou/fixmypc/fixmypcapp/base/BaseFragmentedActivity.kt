package com.kirakishou.fixmypc.fixmypcapp.base

import android.support.v4.app.Fragment
import com.kirakishou.fixmypc.fixmypcapp.R

/**
 * Created by kirakishou on 8/21/2017.
 */
abstract class BaseFragmentedActivity : BaseActivity() {

    fun pushFragment(fragmentTag: String) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = getFragmentFromTag(fragmentTag)
        }

        replaceFragment(fragment, fragmentTag)
    }

    protected fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    protected fun replaceFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit()
    }

    abstract fun getFragmentFromTag(fragmentTag: String): Fragment
}