package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ActiveMalfunctionsListFragmentViewModel

/**
 * Created by kirakishou on 9/9/2017.
 */
class ActiveMalfunctionsListFragmentViewModelFactory(val apiClient: ApiClient) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActiveMalfunctionsListFragmentViewModel(apiClient) as T
    }
}