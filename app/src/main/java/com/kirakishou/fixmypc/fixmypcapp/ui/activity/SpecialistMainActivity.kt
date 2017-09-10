package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class SpecialistMainActivity : BaseActivity<SpecialistMainActivityViewModel>() {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_specialist_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    /*override fun getFragmentFromTag(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST -> ActiveDamageClaimsListFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown fragmentTag: $fragmentTag")
        }
    }*/

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        //mViewModel.initPresenter()

        //pushFragment(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
    }

    override fun onActivityDestroy() {
        //mPresenter.destroyPresenter()

        mRefWatcher.watch(this)
    }

    override fun onViewReady() {
    }

    override fun onViewStop() {
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule())
                .build()
                .inject(this)
    }

    fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }

    fun onUnknownError(error: Throwable) {
    }
}
