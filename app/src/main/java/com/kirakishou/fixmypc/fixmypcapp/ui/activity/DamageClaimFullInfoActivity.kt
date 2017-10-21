package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerDamageClaimFullInfoActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.DamageClaimFullInfoActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.DamageClaimFullInfoActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.DamageClaimFullInfoActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.DamageClaimFullInfoActivityNavigator
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class DamageClaimFullInfoActivity : BaseActivity<DamageClaimFullInfoActivityViewModel>(),
        FragmentManager.OnBackStackChangedListener, BaseActivityFragmentCallback {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: DamageClaimFullInfoActivityViewModelFactory

    @Inject
    lateinit var mNavigator: DamageClaimFullInfoActivityNavigator

    override fun initViewModel(): DamageClaimFullInfoActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(DamageClaimFullInfoActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_damage_claim_full_info
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        supportFragmentManager.addOnBackStackChangedListener(this)
        mNavigator.navigateToDamageClaimFullInfoFragment(intent.extras)
    }

    override fun onActivityDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
        mRefWatcher.watch(this)
    }

    override fun onActivityStart() {
    }

    override fun onActivityStop() {
    }

    override fun resolveDaggerDependency() {
        DaggerDamageClaimFullInfoActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .damageClaimFullInfoActivityModule(DamageClaimFullInfoActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onBackStackChanged() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onBackPressed() {
        mNavigator.popFragment()
    }
}





























