package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists.RespondedSpecialistFullProfileFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists.RespondedSpecialistsListFragment

/**
 * Created by kirakishou on 10/2/2017.
 */
class RespondedSpecialistsActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToRespondedSpecialistsList(damageClaimId: Long) {
        val bundle = Bundle()
        bundle.putLong("damage_claim_id", damageClaimId)

        navigateToFragment(RespondedSpecialistsListFragment::class,
                Constant.FragmentTags.RESPONDED_SPECIALISTS_LIST, bundle)
    }

    fun navigateToSpecialistFullProfileFragment(mDamageClaimId: Long, profile: SpecialistProfile) {
        val args = Bundle()
        args.putLong("user_id", profile.userId)
        args.putString("name", profile.name)
        args.putFloat("rating", profile.rating)
        args.putString("photo_name", profile.photoName)
        args.putString("phone", profile.phone)
        args.putLong("registered_on", profile.registeredOn)
        args.putInt("success_repairs", profile.successRepairs)
        args.putInt("fail_repairs", profile.failRepairs)
        args.putLong("damage_claim_id", mDamageClaimId)

        navigateToFragment(RespondedSpecialistFullProfileFragment::class,
                Constant.FragmentTags.SPECIALIST_FULL_PROFILE, args)
    }
}