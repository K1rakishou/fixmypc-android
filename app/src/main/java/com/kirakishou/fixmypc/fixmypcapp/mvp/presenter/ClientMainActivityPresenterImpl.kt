package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientMainActivityView
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class ClientMainActivityPresenterImpl
    @Inject constructor(): ClientMainActivityPresenter<ClientMainActivityView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("ClientMainActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("ClientMainActivityPresenterImpl.destroyPresenter()")
    }
}