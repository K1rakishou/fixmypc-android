package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.MyDamageClaimsFragmentViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 9/9/2017.
 */
class MyDamageClaimsFragmentViewModelFactory
@Inject constructor(): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyDamageClaimsFragmentViewModel() as T
    }
}