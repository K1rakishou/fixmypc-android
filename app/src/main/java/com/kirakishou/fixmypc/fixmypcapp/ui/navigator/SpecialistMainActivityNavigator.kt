package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.DamageClaimFullInfoFragment

/**
 * Created by kirakishou on 9/11/2017.
 */
class SpecialistMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator() {
    private val fragmentManager = activity.supportFragmentManager

    fun popFragment() {
        fragmentManager.popBackStack()
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

    fun navigateToActiveDamageClaimsListFragment() {
        val fragment = createNewFragmentIfNotInStack<ActiveDamageClaimsListFragment>(fragmentManager,
                Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)

        val isFragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)

        if (isFragmentInStack == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_frame, fragment, Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                    .addToBackStack(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                    .commit()
        } else {
            val visibleFragment = getVisibleFragment()

            fragmentManager.beginTransaction()
                    .hide(visibleFragment)
                    .show(fragment)
                    .addToBackStack(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                    .commit()
        }
    }

    fun navigateToDamageClaimFullInfoFragment(damageClaim: DamageClaim) {
        val bundle = Bundle()
        bundle.putLong("damage_claim_id", damageClaim.id)
        bundle.putBoolean("damage_claim_is_active", damageClaim.isActive)
        bundle.putInt("damage_claim_category", damageClaim.category)
        bundle.putString("damage_claim_description", damageClaim.description)
        bundle.putDouble("damage_claim_lat", damageClaim.lat)
        bundle.putDouble("damage_claim_lon", damageClaim.lon)
        bundle.putLong("damage_claim_created_on", damageClaim.createdOn)
        bundle.putStringArrayList("damage_claim_photo_names", ArrayList(damageClaim.photoNames))

        val fragment = createNewFragmentIfNotInStackWithParams<DamageClaimFullInfoFragment>(fragmentManager,
                Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO, bundle)

        val isFragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)

        if (isFragmentInStack == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
                    .addToBackStack(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
                    .commit()
        } else {
            val visibleFragment = getVisibleFragment()

            fragmentManager.beginTransaction()
                    .hide(visibleFragment)
                    .show(fragment)
                    .addToBackStack(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
                    .commit()
        }
    }
}