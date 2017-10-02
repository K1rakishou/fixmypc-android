package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientMyDamageClaimsFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.RespondedSpecialistsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims.SpecialistFullProfileFragment

/**
 * Created by kirakishou on 9/27/2017.
 */
class ClientMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun popFragment() {
        fragmentManager.popBackStack()
    }

    fun navigateToClientAllDamageClaimsFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.CLIENT_MY_DAMAGE_CLAIMS)
        if (fragmentInStack == null) {
            val newFragment = ClientMyDamageClaimsFragment()

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.CLIENT_MY_DAMAGE_CLAIMS)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
    }

    fun navigateToRespondedSpecialistsListFragment(damageClaimId: Long) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.RESPONDED_SPECIALISTS_LIST)
        if (fragmentInStack == null) {
            val newFragment = RespondedSpecialistsListFragment()
            val args = Bundle()
            args.putLong("damage_claim_id", damageClaimId)

            newFragment.arguments = args

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.RESPONDED_SPECIALISTS_LIST)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
    }

    fun navigateToSpecialistFullProfileFragment(profile: SpecialistProfile) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val visibleFragment = getVisibleFragment()

        if (visibleFragment != null) {
            fragmentTransaction.hide(visibleFragment)
        }

        val fragmentInStack = fragmentManager.findFragmentByTag(Constant.FragmentTags.SPECIALIST_FULL_PROFILE)
        if (fragmentInStack == null) {
            val newFragment = SpecialistFullProfileFragment()
            val args = Bundle()
            args.putLong("user_id", profile.userId)
            args.putString("name", profile.name)
            args.putFloat("rating", profile.rating)
            args.putString("photo_name", profile.photoName)
            args.putLong("registered_on", profile.registeredOn)
            args.putInt("success_repairs", profile.successRepairs)
            args.putInt("fail_repairs", profile.failRepairs)

            newFragment.arguments = args

            fragmentTransaction
                    .add(R.id.fragment_frame, newFragment, Constant.FragmentTags.SPECIALIST_FULL_PROFILE)
                    .addToBackStack(null)
        } else {
            fragmentTransaction
                    .show(fragmentInStack)
                    .addToBackStack(null)
        }

        fragmentTransaction.commit()
    }
}





























