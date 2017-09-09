package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.Toast
import butterknife.BindView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentedActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class ClientMainActivity : BaseFragmentedActivity<ClientMainActivityViewModel>() {

    @BindView(R.id.my_profile_button)
    lateinit var myProfileButton: ImageView

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    override fun getFragmentFromTag(fragmentTag: String): Fragment {
        throw NotImplementedError()
    }

    override fun getViewModel0(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_client_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        //mViewModel.initPresenter()
    }

    override fun onActivityDestroy() {
        //mActivityPresenter.destroyPresenter()

        mRefWatcher.watch(this)
    }

    override fun onViewReady() {

    }

    override fun onViewStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule())
                .build()
                .inject(this)
    }

    fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    fun onUnknownError(error: Throwable) {
        if (error.message != null) {
            showErrorMessageDialog(error.message!!)
        } else {
            showErrorMessageDialog("Неизвестная ошибка")
        }
    }
}
