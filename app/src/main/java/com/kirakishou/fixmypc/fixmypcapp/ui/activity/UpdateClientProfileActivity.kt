package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateClientProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateClientProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateClientProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateSpecialistProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateClientProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateSpecialistProfileActivityViewModelFactory
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class UpdateClientProfileActivity : BaseActivity<UpdateClientProfileActivityViewModel>() {

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: UpdateClientProfileActivityViewModelFactory

    override fun initViewModel(): UpdateClientProfileActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(UpdateClientProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_update_client_profile
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
        DaggerUpdateClientProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateClientProfileActivityModule(UpdateClientProfileActivityModule(this))
                .build()
                .inject(this)
    }

}
