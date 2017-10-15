package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.DamageClaimFullInfoFragment

/**
 * Created by kirakishou on 10/15/2017.
 */
class DamageClaimFullInfoActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToDamageClaimFullInfoFragment(args: Bundle) {
        navigateToFragment(DamageClaimFullInfoFragment::class,
                Constant.FragmentTags.DAMAGE_CLAIM_FULL_INFO, args)
    }
}