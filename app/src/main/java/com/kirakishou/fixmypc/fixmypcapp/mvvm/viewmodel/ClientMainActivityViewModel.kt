package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class ClientMainActivityViewModel
    @Inject constructor(): BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()

        Timber.e("ClientMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }
}