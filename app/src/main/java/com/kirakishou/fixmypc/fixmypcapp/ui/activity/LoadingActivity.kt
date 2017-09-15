package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoadingActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoadingActivityViewModelFactory
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class LoadingActivity : BaseActivity<LoadingActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactory: LoadingActivityViewModelFactory

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    @Inject
    lateinit var mAppSettings: AppSettings

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val accountInfoPrefs by lazy { mAppSharedPreference.prepare<AccountInfoPreference>() }

    override fun initViewModel(): LoadingActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(LoadingActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.activity_loading
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mCompositeDisposable += getViewModel().mOutputs.runClientMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runClientMainActivity(it.sessionId, it.accountType) })

        mCompositeDisposable += getViewModel().mOutputs.runGuestMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runGuestMainActivity() })

        mCompositeDisposable += getViewModel().mOutputs.runSpecialistMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runSpecialistMainActivity(it.sessionId, it.accountType) })

        mCompositeDisposable += getViewModel().mErrors.onCouldNotConnectToServer()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onCouldNotConnectToServer(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    override fun onResume() {
        super.onResume()
        accountInfoPrefs.load()
    }

    override fun onPause() {
        super.onPause()
        accountInfoPrefs.save()
    }

    override fun onViewReady() {
        //FIXME: accountInfoPrefs should be loaded from preferences via accountInfoPrefs.load()
        //don't forger to delete the following:
        accountInfoPrefs.login = Fickle.of("test2@gmail.com")
        accountInfoPrefs.password = Fickle.of("1234567890")

        if (accountInfoPrefs.exists()) {
            val login = accountInfoPrefs.login.get()
            val password = accountInfoPrefs.password.get()

            getViewModel().mInputs.startLoggingIn(LoginPasswordDTO(login, password))
        } else {
            accountInfoPrefs.clear()
            runGuestMainActivity()
        }
    }

    override fun onViewStop() {

    }

    override fun onActivityDestroy() {
        mRefWatcher.watch(this)
    }

    private fun runGuestMainActivity() {
        Timber.e("Running guest MainActivity")
    }

    private fun runClientMainActivity(sessionId: String, accountType: AccountType) {
        Timber.e("Running client MainActivity")
        runActivity(ClientNewDamageClaimActivity::class.java, true)
    }

    private fun runSpecialistMainActivity(sessionId: String, accountType: AccountType) {
        Timber.e("Running specialist MainActivity")
        runActivity(SpecialistMainActivity::class.java, true)
    }

    private fun onCouldNotConnectToServer(error: Throwable) {
        Timber.e(error)

        //TODO: show reconnection button
    }

    override fun onUnknownError(error: Throwable) {
        super.onUnknownError(error)
    }

    private fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun resolveDaggerDependency() {
        DaggerLoadingActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loadingActivityModule(LoadingActivityModule())
                .build()
                .inject(this)
    }
}




















































