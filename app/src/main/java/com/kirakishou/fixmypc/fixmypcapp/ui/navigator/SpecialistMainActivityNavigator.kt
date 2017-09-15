package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment

/**
 * Created by kirakishou on 9/11/2017.
 */
class SpecialistMainActivityNavigator(activity: AppCompatActivity) {
    private val fragmentManager = activity.supportFragmentManager

    fun popFragment(): Boolean {
        val backStackSize = fragmentManager.backStackEntryCount

        fragmentManager.popBackStack()
        return backStackSize <= 1
    }

    fun navigateToActiveDamageClaimsListFragment() {
        val fragment = ActiveDamageClaimsListFragment.newInstance()
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                .addToBackStack(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                .commit()
    }
}