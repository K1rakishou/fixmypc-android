package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoginActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoginActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoginActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoginActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.LoginActivityNavigator
import javax.inject.Inject

class LoginActivity : BaseActivity<LoginActivityViewModel>(),
        FragmentManager.OnBackStackChangedListener{

    @Inject
    lateinit var mViewModelFactory: LoginActivityViewModelFactory

    @Inject
    lateinit var mNavigator: LoginActivityNavigator

    override fun initViewModel(): LoginActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(LoginActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_login
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        supportFragmentManager.addOnBackStackChangedListener(this)
        mNavigator.navigateToLoginFragment()
    }

    override fun onActivityDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
    }

    override fun onActivityStart() {
    }

    override fun onActivityStop() {
    }

    override fun onBackStackChanged() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun resolveDaggerDependency() {
        DaggerLoginActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loginActivityModule(LoginActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onBackPressed() {
        mNavigator.popFragment()
    }
}



























