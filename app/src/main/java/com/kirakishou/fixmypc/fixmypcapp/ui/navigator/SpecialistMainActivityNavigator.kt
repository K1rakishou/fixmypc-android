package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.DamageClaimFullInfoFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.SpecialistProfileFragment

/**
 * Created by kirakishou on 9/11/2017.
 */
class SpecialistMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToActiveDamageClaimsListFragment() {
        navigateToFragment(ActiveDamageClaimsListFragment::class,
                Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
    }

    fun navigateToSpecialistProfileFragment() {
        navigateToFragment(SpecialistProfileFragment::class,
                Constant.FragmentTags.SPECIALIST_PROFILE)
    }
}


































