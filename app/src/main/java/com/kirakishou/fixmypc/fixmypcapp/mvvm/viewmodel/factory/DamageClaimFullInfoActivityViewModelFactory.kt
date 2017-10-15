package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.DamageClaimFullInfoActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 10/15/2017.
 */
class DamageClaimFullInfoActivityViewModelFactory
@Inject constructor(val apiClient: ApiClient,
                    val appSettings: AppSettings,
                    val schedulers: SchedulerProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DamageClaimFullInfoActivityViewModel(apiClient, appSettings, schedulers) as T
    }
}