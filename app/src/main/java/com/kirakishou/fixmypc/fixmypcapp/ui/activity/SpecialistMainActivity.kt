package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import android.widget.Toast
import butterknife.BindView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.PermissionGrantedCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.RequestPermissionCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import javax.inject.Inject

class SpecialistMainActivity : BaseActivity<SpecialistMainActivityViewModel>(), BaseActivityFragmentCallback,
        FragmentManager.OnBackStackChangedListener, BottomNavigationView.OnNavigationItemSelectedListener, RequestPermissionCallback {

    @BindView(R.id.bottom_navigation)
    lateinit var bottomNavigationView: BottomNavigationView

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    @Inject
    lateinit var mPermissionManager: PermissionManager

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_specialist_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        supportFragmentManager.addOnBackStackChangedListener(this)
        getViewModel().init()

        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            mNavigator.navigateToActiveDamageClaimsListFragment()
        }
    }

    override fun onActivityDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
        mRefWatcher.watch(this)
    }

    override fun onActivityStart() {

    }

    override fun onActivityStop() {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.damage_claim_list -> {
                getViewModel().currentFragmentTag = Constant.FragmentTags.ACTIVE_DAMAGE_CLAIMS_LIST
                mNavigator.navigateToActiveDamageClaimsListFragment()
            }

            R.id.profile -> {
                getViewModel().currentFragmentTag = Constant.FragmentTags.SPECIALIST_PROFILE
                mNavigator.navigateToSpecialistProfileFragment()
            }

            R.id.options -> {
                Timber.e("Navigate to options fragment")
            }
        }
        return true
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
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String, duration: Int) {
        super.onShowToast(message, duration)
    }

    override fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean) {
        super.runActivity(clazz, finishCurrentActivity)
    }

    override fun onUnknownError(error: Throwable) {
        super.onUnknownError(error)
    }

    override fun onBackStackChanged() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onBackPressed() {
        mNavigator.popFragment()
    }
}
