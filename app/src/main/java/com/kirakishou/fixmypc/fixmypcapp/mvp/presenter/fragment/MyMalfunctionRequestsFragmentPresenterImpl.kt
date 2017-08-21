package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment

import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.MyMalfunctionRequestsFragmentView
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class MyMalfunctionRequestsFragmentPresenterImpl
    @Inject constructor(): MyMalfunctionRequestsFragmentPresenter<MyMalfunctionRequestsFragmentView>() {

    override fun initPresenter() {
        Timber.d("MyMalfunctionRequestsFragmentPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        Timber.d("MyMalfunctionRequestsFragmentPresenterImpl.destroyPresenter()")
    }
}