package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateSpecialistProfileActivityViewModel
import javax.inject.Inject

/**
 * Created by kirakishou on 10/10/2017.
 */
class UpdateSpecialistProfileActivityViewModelFactory
@Inject constructor(val apiClient: ApiClient,
                    val schedulers: SchedulerProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
        return UpdateSpecialistProfileActivityViewModel(apiClient, schedulers) as T
    }
}