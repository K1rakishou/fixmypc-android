package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity

import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.SpecialistMainActivityView
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 9/3/2017.
 */
class SpecialistMainActivityPresenterImpl
    @Inject constructor() : SpecialistMainActivityPresenter<SpecialistMainActivityView>() {

    override fun initPresenter() {
        Timber.d("SpecialistMainActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        Timber.d("SpecialistMainActivityPresenterImpl.destroyPresenter()")
    }
}