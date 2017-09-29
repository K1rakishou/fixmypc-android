package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ClientMainActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ClientMainActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ClientMainActivityOutputs
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class ClientMainActivityViewModel
@Inject constructor() : BaseViewModel(),
        ClientMainActivityInputs,
        ClientMainActivityOutputs,
        ClientMainActivityErrors {

    val mInputs: ClientMainActivityInputs = this
    val mOutputs: ClientMainActivityOutputs = this
    val mErrors: ClientMainActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    fun init() {

    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("ClientMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }
}