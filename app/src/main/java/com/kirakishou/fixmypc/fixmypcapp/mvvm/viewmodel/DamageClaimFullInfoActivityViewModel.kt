package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/15/2017.
 */
class DamageClaimFullInfoActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        Timber.e("DamageClaimFullInfoActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }
}