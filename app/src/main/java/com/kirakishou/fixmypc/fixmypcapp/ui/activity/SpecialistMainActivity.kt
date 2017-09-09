package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentedActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist.ActiveDamageClaimsListFragment
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class SpecialistMainActivity : BaseFragmentedActivity<SpecialistMainActivityViewModel>() {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    override fun getViewModelFactory(): ViewModelProvider.Factory = mViewModelFactory
    override fun getContentView() = R.layout.activity_specialist_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun getFragmentFromTag(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST -> ActiveDamageClaimsListFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown fragmentTag: $fragmentTag")
        }
    }

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        //mViewModel.initPresenter()

        pushFragment(Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST)
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
