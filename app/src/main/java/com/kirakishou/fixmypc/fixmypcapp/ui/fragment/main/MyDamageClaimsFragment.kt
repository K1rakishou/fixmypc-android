package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerMyDamageClaimsFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.MyDamageClaimsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.MyDamageClaimsFragmentViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.MyDamageClaimsFragmentViewModelFactory
import javax.inject.Inject

class MyDamageClaimsFragment  : BaseFragment<MyDamageClaimsFragmentViewModel>() {

    @Inject
    lateinit var mViewModelFactory: MyDamageClaimsFragmentViewModelFactory

    override fun initViewModel(): MyDamageClaimsFragmentViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(MyDamageClaimsFragmentViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_my_damage_claims
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {

    }

    override fun onFragmentViewDestroy() {

    }

    fun onShowToast(message: String, duration: Int) {
        showToast(message, duration)
    }

    fun onUnknownError(throwable: Throwable) {
        unknownError(throwable)
    }

    override fun resolveDaggerDependency() {
        DaggerMyDamageClaimsFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .myDamageClaimsFragmentModule(MyDamageClaimsFragmentModule())
                .build()
                .inject(this)
    }
}
