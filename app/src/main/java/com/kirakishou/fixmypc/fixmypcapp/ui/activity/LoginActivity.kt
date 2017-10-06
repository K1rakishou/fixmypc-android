package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoginActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoginActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoginActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoginActivityViewModelFactory
import javax.inject.Inject

class LoginActivity : BaseActivity<LoginActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactory: LoginActivityViewModelFactory

    override fun initViewModel(): LoginActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(LoginActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_login
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
    }

    override fun onActivityDestroy() {
    }

    override fun onActivityStart() {
    }

    override fun onActivityStop() {
    }

    override fun resolveDaggerDependency() {
        DaggerLoginActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loginActivityModule(LoginActivityModule())
                .build()
                .inject(this)
    }

}
