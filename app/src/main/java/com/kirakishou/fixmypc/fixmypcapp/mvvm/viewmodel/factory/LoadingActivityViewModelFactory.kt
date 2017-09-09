package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoadingActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 9/9/2017.
 */
class LoadingActivityViewModelFactory
    @Inject constructor(val apiClient: ApiClient,
                        val appSettings: AppSettings): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoadingActivityViewModel(apiClient, appSettings) as T
    }

}