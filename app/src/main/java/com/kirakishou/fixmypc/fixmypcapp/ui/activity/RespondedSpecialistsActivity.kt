package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerRespondedSpecialistsActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.RespondedSpecialistsActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.RespondedSpecialistsViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.RespondedSpecialistsActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.RespondedSpecialistsActivityNavigator
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class RespondedSpecialistsActivity : BaseActivity<RespondedSpecialistsViewModel>(),
        FragmentManager.OnBackStackChangedListener {

    @Inject
    lateinit var mViewModelFactory: RespondedSpecialistsActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mNavigator: RespondedSpecialistsActivityNavigator

    private var mDamageClaimId: Long = -1L

    override fun initViewModel(): RespondedSpecialistsViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(RespondedSpecialistsViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_responded_specialists
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        supportFragmentManager.addOnBackStackChangedListener(this)

        getDamageClaimId(intent)
        getViewModel().init()

        if (mDamageClaimId == -1L) {
            throw IllegalArgumentException("mDamageClaimId == -1L")
        }

        mNavigator.navigateToRespondedSpecialistsList(mDamageClaimId)
    }

    private fun getDamageClaimId(intent: Intent) {
        val params = intent.extras
        mDamageClaimId = params.getLong("damage_claim_id")
    }

    override fun onActivityDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
        mRefWatcher.watch(this)
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
        DaggerRespondedSpecialistsActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .respondedSpecialistsActivityModule(RespondedSpecialistsActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onBackPressed() {
        mNavigator.popFragment()
    }
}





























