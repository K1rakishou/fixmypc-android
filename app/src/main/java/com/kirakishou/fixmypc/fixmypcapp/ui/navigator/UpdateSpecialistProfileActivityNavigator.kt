package com.kirakishou.fixmypc.fixmypcapp.ui.navigator

import android.support.v7.app.AppCompatActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseNavigator
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.UpdateSpecialistProfileFragment

/**
 * Created by kirakishou on 10/10/2017.
 */
class UpdateSpecialistProfileActivityNavigator(activity: AppCompatActivity) : BaseNavigator(activity) {

    fun navigateToUpdateSpecialistProfileFragment() {
        navigateToFragment(UpdateSpecialistProfileFragment::class,
                Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
    }
}