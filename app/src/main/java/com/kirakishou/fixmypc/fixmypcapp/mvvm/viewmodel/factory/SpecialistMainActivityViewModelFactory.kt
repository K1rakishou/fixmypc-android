package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel

/**
 * Created by kirakishou on 9/9/2017.
 */
class SpecialistMainActivityViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SpecialistMainActivityViewModel() as T
    }
}