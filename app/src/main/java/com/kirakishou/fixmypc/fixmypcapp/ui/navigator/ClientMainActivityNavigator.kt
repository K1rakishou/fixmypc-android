package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientMyDamageClaimsFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientOptionsFragment
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.ClientProfileFragment

/**
 * Created by kirakishou on 9/27/2017.
 */
class ClientMainActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToClientMyDamageClaimsFragment() {
        navigateToFragment(ClientMyDamageClaimsFragment::class,
                Constant.FragmentTags.CLIENT_MY_DAMAGE_CLAIMS)
    }

    fun navigateToClientProfileFragment() {
        navigateToFragment(ClientProfileFragment::class,
                Constant.FragmentTags.CLIENT_PROFILE)
    }

    fun navigateToClientOptionsFragment() {
        navigateToFragment(ClientOptionsFragment::class,
                Constant.FragmentTags.CLIENT_OPTIONS)
    }
}





























