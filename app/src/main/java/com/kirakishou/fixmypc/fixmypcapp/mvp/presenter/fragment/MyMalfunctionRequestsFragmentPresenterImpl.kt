package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.fragment

import com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment.MyDamageClaimsFragmentView
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class MyMalfunctionRequestsFragmentPresenterImpl
    @Inject constructor(): MyMalfunctionRequestsFragmentPresenter<MyDamageClaimsFragmentView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("MyMalfunctionRequestsFragmentPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("MyMalfunctionRequestsFragmentPresenterImpl.destroyPresenter()")
    }
}