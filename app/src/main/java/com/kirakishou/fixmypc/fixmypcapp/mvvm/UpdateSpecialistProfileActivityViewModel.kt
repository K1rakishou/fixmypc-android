package com.kirakishou.fixmypc.fixmypcapp.mvvm

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/10/2017.
 */
class UpdateSpecialistProfileActivityViewModel
@Inject constructor(val mApiClient: ApiClient,
                    val mSchedulers: SchedulerProvider) : BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        Timber.e("UpdateSpecialistProfileActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }
}