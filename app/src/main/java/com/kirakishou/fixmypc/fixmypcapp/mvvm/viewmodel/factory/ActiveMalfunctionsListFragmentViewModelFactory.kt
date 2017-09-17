package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtilsImpl
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ActiveMalfunctionsListFragmentViewModel

/**
 * Created by kirakishou on 9/9/2017.
 */
class ActiveMalfunctionsListFragmentViewModelFactory(val apiClient: ApiClient,
                                                     val wifiUtils: WifiUtilsImpl,
                                                     val damageClaimRepo: DamageClaimRepository,
                                                     val schedulers: SchedulerProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActiveMalfunctionsListFragmentViewModel(apiClient, wifiUtils, damageClaimRepo, schedulers) as T
    }
}