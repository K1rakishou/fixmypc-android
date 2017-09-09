package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel

/**
 * Created by kirakishou on 9/9/2017.
 */
class ClientMainActivityViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientMainActivityViewModel() as T
    }
}