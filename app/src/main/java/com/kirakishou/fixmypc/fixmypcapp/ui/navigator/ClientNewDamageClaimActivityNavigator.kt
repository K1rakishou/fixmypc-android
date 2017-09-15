package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimCategoryFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimDescriptionFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimLocationFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimPhotosFragment

/**
 * Created by kirakishou on 9/10/2017.
 */
class ClientNewDamageClaimActivityNavigator(activity: AppCompatActivity) : BaseNavigator() {
    private val fragmentManager = activity.supportFragmentManager

    fun popFragment(): Boolean {
        fragmentManager.popBackStack()
        return fragmentManager.backStackEntryCount <= 0
    }

    fun navigateToDamageClaimCategoryFragment() {
        val fragment = createNewFragmentIfNotInStack<DamageClaimCategoryFragment>(fragmentManager,
                Constant.FragmentTags.DAMAGE_CATEGORY, DamageClaimCategoryFragment::class.java)

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.DAMAGE_CATEGORY)
                .addToBackStack(Constant.FragmentTags.DAMAGE_CATEGORY)
                .commit()
    }

    fun navigateToDamageClaimDescriptionFragment() {
        val fragment = createNewFragmentIfNotInStack<DamageClaimDescriptionFragment>(fragmentManager,
                Constant.FragmentTags.DAMAGE_DESCRIPTION, DamageClaimDescriptionFragment::class.java)

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.DAMAGE_DESCRIPTION)
                .addToBackStack(Constant.FragmentTags.DAMAGE_DESCRIPTION)
                .commit()
    }

    fun navigateToDamageClaimPhotosFragment() {
        val fragment = createNewFragmentIfNotInStack<DamageClaimPhotosFragment>(fragmentManager,
                Constant.FragmentTags.DAMAGE_PHOTOS, DamageClaimPhotosFragment::class.java)

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.DAMAGE_PHOTOS)
                .addToBackStack(Constant.FragmentTags.DAMAGE_PHOTOS)
                .commit()
    }

    fun navigateToDamageClaimLocationFragment() {
        val fragment = createNewFragmentIfNotInStack<DamageClaimLocationFragment>(fragmentManager,
                Constant.FragmentTags.DAMAGE_LOCATION, DamageClaimLocationFragment::class.java)

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment, Constant.FragmentTags.DAMAGE_LOCATION)
                .addToBackStack(Constant.FragmentTags.DAMAGE_LOCATION)
                .commit()
    }
}

































