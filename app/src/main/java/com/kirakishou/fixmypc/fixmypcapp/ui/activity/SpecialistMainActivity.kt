package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity.SpecialistMainActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.SpecialistMainActivityView
import javax.inject.Inject

class SpecialistMainActivity : BaseActivity(), SpecialistMainActivityView {

    @Inject
    lateinit var mPresenter: SpecialistMainActivityPresenterImpl

    override fun getContentView() = R.layout.activity_specialist_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mPresenter.initPresenter()
    }

    override fun onActivityDestroy() {
        mPresenter.destroyPresenter()
    }

    override fun onViewReady() {
    }

    override fun onViewStop() {
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
    }
}
