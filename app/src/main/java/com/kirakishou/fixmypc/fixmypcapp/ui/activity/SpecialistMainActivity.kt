package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import javax.inject.Inject

class SpecialistMainActivity : BaseActivity<SpecialistMainActivityViewModel>(), BaseActivityFragmentCallback {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_specialist_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mNavigator.navigateToActiveDamageClaimsListFragment()
    }

    override fun onActivityDestroy() {
        mRefWatcher.watch(this)
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

    override fun onShowToast(message: String, duration: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        super.onUnknownError(error)
    }

    override fun onBackPressed() {
        if (mNavigator.popFragment()) {
            super.onBackPressed()
        }
    }
}
