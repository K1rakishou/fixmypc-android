package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateSpecialistProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateSpecialistProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateSpecialistProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateSpecialistProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.PermissionGrantedCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.RequestPermissionCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateSpecialistProfileActivityNavigator
import javax.inject.Inject

class UpdateSpecialistProfileActivity : BaseActivity<UpdateSpecialistProfileActivityViewModel>(),
        RequestPermissionCallback, BaseActivityFragmentCallback {

    @Inject
    lateinit var mPermissionManager: PermissionManager

    @Inject
    lateinit var mViewModelFactory: UpdateSpecialistProfileActivityViewModelFactory

    @Inject
    lateinit var mNavigator: UpdateSpecialistProfileActivityNavigator

    override fun initViewModel(): UpdateSpecialistProfileActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(UpdateSpecialistProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_update_specialist_profile
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        getViewModel().init()

        val args = intent.extras
        mNavigator.navigateToUpdateSpecialistProfileFragment(args)
    }

    override fun onActivityDestroy() {
    }

    override fun onActivityStart() {
    }

    override fun onActivityStop() {
    }

    override fun requestPermission(permission: String, requestCode: Int) {
        mPermissionManager.askForPermission(this, permission, requestCode) { granted ->
            if (granted) {
                val visibleFragment = mNavigator.getVisibleFragment()
                        ?: throw NullPointerException("visibleFragment == null")

                if (visibleFragment is PermissionGrantedCallback) {
                    visibleFragment.onPermissionGranted()
                } else {
                    throw IllegalStateException("currentFragment does not implement PermissionGrantedCallback")
                }

            } else {
                onShowToast("Не удалось получить разрешение на открытие галлереи фото", Toast.LENGTH_LONG)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun resolveDaggerDependency() {
        DaggerUpdateSpecialistProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateSpecialistProfileActivityModule(UpdateSpecialistProfileActivityModule(this))
                .build()
                .inject(this)
    }
}



























