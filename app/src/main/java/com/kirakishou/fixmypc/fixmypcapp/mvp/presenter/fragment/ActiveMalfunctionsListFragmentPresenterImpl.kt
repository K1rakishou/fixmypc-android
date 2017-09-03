package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment

import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.ActiveMalfunctionsListFragmentView
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class ActiveMalfunctionsListFragmentPresenterImpl
    @Inject constructor() : ActiveMalfunctionsListFragmentPresenter<ActiveMalfunctionsListFragmentView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("ActiveMalfunctionsListFragmentPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("ActiveMalfunctionsListFragmentPresenterImpl.destroyPresenter()")
    }
}