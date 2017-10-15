package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.new_damage_claim.*

/**
 * Created by kirakishou on 9/10/2017.
 */
class ClientNewDamageClaimActivityNavigator(activity: AppCompatActivity)
    : BaseNavigator(activity) {

    fun navigateToDamageClaimCategoryFragment() {
        navigateToFragment(DamageClaimCategoryFragment::class,
                Constant.FragmentTags.DAMAGE_CATEGORY)
    }

    fun navigateToDamageClaimDescriptionFragment() {
        navigateToFragment(DamageClaimDescriptionFragment::class,
                Constant.FragmentTags.DAMAGE_DESCRIPTION)
    }

    fun navigateToDamageClaimPhotosFragment() {
        navigateToFragment(DamageClaimPhotosFragment::class,
                Constant.FragmentTags.DAMAGE_PHOTOS)
    }

    fun navigateToDamageClaimLocationFragment() {
        navigateToFragment(DamageClaimLocationFragment::class,
                Constant.FragmentTags.DAMAGE_LOCATION)
    }

    fun navigateToDamageClaimSendRequestFragment() {
        navigateToFragment(DamageClaimSendRequestFragment::class,
                Constant.FragmentTags.DAMAGE_SEND_REQUEST)
    }
}

































