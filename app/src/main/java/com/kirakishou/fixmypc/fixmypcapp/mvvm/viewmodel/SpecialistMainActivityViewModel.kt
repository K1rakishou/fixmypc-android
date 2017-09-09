package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class SpecialistMainActivityViewModel
    @Inject constructor() : BaseViewModel() {

    override fun onCleared() {
        super.onCleared()
        Timber.e("MyDamageClaimsFragmentViewModel.onCleared()")
    }
}