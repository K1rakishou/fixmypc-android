package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewMalfunctionActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 9/9/2017.
 */
class ClientNewMalfunctionActivityViewModelFactory
    @Inject constructor(val apiClient: ApiClient): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientNewMalfunctionActivityViewModel(apiClient) as T
    }
}