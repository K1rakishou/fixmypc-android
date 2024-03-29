package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import android.widget.ImageView
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientMainActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ClientMainActivity : BaseActivity<ClientMainActivityViewModel>(), FragmentManager.OnBackStackChangedListener,
    BaseActivityFragmentCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottom_nav_menu)
    lateinit var bottomNavigationMenu: BottomNavigationView

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientMainActivityNavigator

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.activity_client_main
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        supportFragmentManager.addOnBackStackChangedListener(this)
        initRx()

        bottomNavigationMenu.setOnNavigationItemSelectedListener(this)
        mNavigator.navigateToClientMyDamageClaimsFragment()
    }

    private fun initRx() {

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
                mNavigator.navigateToClientMyDamageClaimsFragment()
            }

            R.id.profile -> {
                mNavigator.navigateToClientProfileFragment()
            }

            R.id.options -> {
                mNavigator.navigateToClientOptionsFragment()
            }
        }
        return true
    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(this))
                .build()
                .inject(this)
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
