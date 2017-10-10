package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.LoadingIndicatorFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.DamageClaimFullInfoFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.SpecialistProfileFragment

/**
 * Created by kirakishou on 9/11/2017.
 */
class SpecialistMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun popFragment() {
        val currentFragment = getVisibleFragment()
        if (currentFragment != null) {
            if (currentFragment !is LoadingIndicatorFragment) {
                fragmentManager.popBackStack()
            }
        }
    }

    fun navigateToActiveDamageClaimsListFragment() {
        navigateToFragment(ActiveDamageClaimsListFragment::class,
                Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
    }

    fun navigateToSpecialistProfileFragment() {
        navigateToFragment(SpecialistProfileFragment::class,
                Constant.FragmentTags.SPECIALIST_PROFILE)
    }

    fun navigateToDamageClaimFullInfoFragment(damageClaim: DamageClaim) {
        val args = Bundle()
        args.putLong("damage_claim_id", damageClaim.id)
        args.putLong("damage_claim_owner_id", damageClaim.ownerId)
        args.putBoolean("damage_claim_is_active", damageClaim.isActive)
        args.putInt("damage_claim_category", damageClaim.category)
        args.putString("damage_claim_description", damageClaim.description)
        args.putDouble("damage_claim_lat", damageClaim.lat)
        args.putDouble("damage_claim_lon", damageClaim.lon)
        args.putLong("damage_claim_created_on", damageClaim.createdOn)
        args.putStringArrayList("damage_claim_photo_names", ArrayList(damageClaim.photoNames))

        navigateToFragment(DamageClaimFullInfoFragment::class,
                Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO, args)
    }
}


































