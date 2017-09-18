package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 9/9/2017.
 */
class ClientNewMalfunctionActivityViewModelFactory
    @Inject constructor(val apiClient: ApiClient,
                        val wifiUtils: WifiUtils,
                        val schedulers: SchedulerProvider): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientNewDamageClaimActivityViewModel(apiClient, wifiUtils, schedulers) as T
    }
}