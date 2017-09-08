package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class ClientMainActivityPresenterImpl
    @Inject constructor(): BaseViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }
}