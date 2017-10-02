package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerRespondedSpecialistsActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.RespondedSpecialistsActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.RespondedSpecialistsViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.RespondedSpecialistsActivityViewModelFactory
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class RespondedSpecialistsActivity : BaseActivity<RespondedSpecialistsViewModel>() {

    @Inject
    lateinit var mViewModelFactory: RespondedSpecialistsActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    override fun initViewModel(): RespondedSpecialistsViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(RespondedSpecialistsViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_responded_specialists
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        getViewModel().init()
    }

    override fun onActivityDestroy() {
        mRefWatcher.watch(this)
    }

    override fun onActivityStart() {
    }

    override fun onActivityStop() {
    }

    override fun resolveDaggerDependency() {
        DaggerRespondedSpecialistsActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .respondedSpecialistsActivityModule(RespondedSpecialistsActivityModule(this))
                .build()
                .inject(this)
    }
}
