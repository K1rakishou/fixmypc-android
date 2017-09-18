package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientNewDamageClaimActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.dialog.ProgressDialog
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.DamageClaimPhotosFragmentCallbacks
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject

class ClientNewDamageClaimActivity : BaseActivity<ClientNewDamageClaimActivityViewModel>(),
        ClientNewMalfunctionActivityFragmentCallback {

    @Inject
    lateinit var mPermissionManager: PermissionManager

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    private lateinit var progressDialog: ProgressDialog

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_client_new_malfunction
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mNavigator.navigateToDamageClaimCategoryFragment()
        progressDialog = ProgressDialog(this)

        getViewModel().mOutputs.uploadProgressUpdateSubject()
                .subscribe(progressDialog.progressUpdateSubject)
    }

    override fun onActivityDestroy() {
        progressDialog.dismiss()
        mRefWatcher.watch(this)
    }

    override fun requestPermission(permission: String, requestCode: Int) {
        mPermissionManager.askForPermission(this, permission, requestCode) { granted ->
            if (granted) {
                val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)

                if (currentFragment is DamageClaimPhotosFragmentCallbacks) {
                    currentFragment.onPermissionGranted()
                }

            } else {
                showToast("Не удалось получить разрешение на открытие галлереи фото", Toast.LENGTH_LONG)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewReady() {

    }

    override fun onViewStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String, duration: Int) {
        showToast(message, duration)
    }

    override fun startActivity(activityClass: Class<*>, finishCurrentActivity: Boolean) {
        runActivity(activityClass, finishCurrentActivity)
    }

    override fun onUnknownError(error: Throwable) {
        super.onUnknownError(error)
    }

    override fun onBackPressed() {
        if (mNavigator.popFragment()) {
            super.onBackPressed()
        }
    }
}
