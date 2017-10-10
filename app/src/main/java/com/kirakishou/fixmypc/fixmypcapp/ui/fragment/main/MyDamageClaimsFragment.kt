package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.main


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerMyDamageClaimsFragmentComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.MyDamageClaimsFragmentModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.MyDamageClaimsFragmentViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.MyDamageClaimsFragmentViewModelFactory
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class MyDamageClaimsFragment : BaseFragment<MyDamageClaimsFragmentViewModel>() {

    @Inject
    lateinit var mViewModelFactory: MyDamageClaimsFragmentViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    override fun initViewModel(): MyDamageClaimsFragmentViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(MyDamageClaimsFragmentViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_my_damage_claims
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {

    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    fun onShowToast(message: String, duration: Int) {
        showToast(message, duration)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerMyDamageClaimsFragmentComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .myDamageClaimsFragmentModule(MyDamageClaimsFragmentModule())
                .build()
                .inject(this)
    }
}
