package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 9/9/2017.
 */
class ClientMainActivityViewModelFactory
    @Inject constructor(val apiClient: ApiClient,
                        val mAppSettings: AppSettings,
                        val mSchedulers: SchedulerProvider): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientMainActivityViewModel(apiClient, mAppSettings, mSchedulers) as T
    }
}