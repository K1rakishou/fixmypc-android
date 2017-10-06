package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.LoginActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.LoginActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.LoginActivityOutputs
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/6/2017.
 */
class LoginActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        LoginActivityInputs,
        LoginActivityOutputs,
        LoginActivityErrors {

    val mInputs: LoginActivityInputs = this
    val mOutputs: LoginActivityOutputs = this
    val mErrors: LoginActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        Timber.e("LoadingActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }
}