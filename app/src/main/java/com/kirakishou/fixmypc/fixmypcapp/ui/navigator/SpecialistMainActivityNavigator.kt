package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
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
class SpecialistMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToActiveDamageClaimsListFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
        if (fragmentInStack == null) {
            val newFragment = ActiveDamageClaimsListFragment()

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
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

        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
        if (fragmentInStack == null) {
            val newFragment = DamageClaimFullInfoFragment()
            newFragment.arguments = bundle

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
    }
}


































