package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication

import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateClientProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateClientProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateClientProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateClientProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateClientProfileActivityNavigator
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class UpdateClientProfileFragment : BaseFragment<UpdateClientProfileActivityViewModel>() {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: UpdateClientProfileActivityViewModelFactory

    @Inject
    lateinit var mNavigator: UpdateClientProfileActivityNavigator

    override fun initViewModel(): UpdateClientProfileActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(UpdateClientProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_update_client_profile
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerUpdateClientProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateClientProfileActivityModule(UpdateClientProfileActivityModule(activity as UpdateClientProfileActivity))
                .build()
                .inject(this)
    }
}


























