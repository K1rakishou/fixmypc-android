package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.UpdateClientProfileFragment

/**
 * Created by kirakishou on 10/20/2017.
 */
class UpdateClientProfileActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToUpdateClientProfileFragment() {
        navigateToFragment(UpdateClientProfileFragment::class,
                Constant.FragmentTags.UPDATE_CLIENT_PROFILE)
    }
}