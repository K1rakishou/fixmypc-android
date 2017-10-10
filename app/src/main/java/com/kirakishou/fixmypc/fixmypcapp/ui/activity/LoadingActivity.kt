package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
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

    override fun onPause() {
        super.onPause()
        accountInfoPrefs.save()
    }

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        accountInfoPrefs.load()
        getViewModel().init()
        initRx()

        if (accountInfoPrefs.exists()) {
            val login = accountInfoPrefs.login.get()
            val password = accountInfoPrefs.password.get()

            getViewModel().mInputs.startLoggingIn(LoginPasswordDTO(login, password))
        } else {
            accountInfoPrefs.clear()
            runLoginActivity()
        }
    }

    override fun onActivityDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += getViewModel().mOutputs.runClientMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runClientMainActivity(it.sessionId, it.accountType) })

        mCompositeDisposable += getViewModel().mOutputs.runSpecialistMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runSpecialistMainActivity(it.sessionId, it.accountType) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    override fun onActivityStart() {

    }

    override fun onActivityStop() {

    }

    private fun runLoginActivity() {
        Timber.e("Running LoginActivity")
        runActivity(LoginActivity::class.java, true)
    }

    private fun runClientMainActivity(sessionId: String, accountType: AccountType) {
        Timber.e("Running client MainActivity")
        runActivity(ClientMainActivity::class.java, true)
    }

    private fun runSpecialistMainActivity(sessionId: String, accountType: AccountType) {
        Timber.e("Running specialist MainActivity")
        runActivity(SpecialistMainActivity::class.java, true)
    }

    private fun onCouldNotConnectToServer() {
        //TODO: show reconnection button
    }

    private fun onBadResponse(errorCode: ErrorCode.Remote) {
        when (errorCode) {
            ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD -> {
                accountInfoPrefs.clear()
                runLoginActivity()
            }

            ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
            ErrorCode.Remote.REC_TIMEOUT -> {
                onCouldNotConnectToServer()
            }

            else -> throw IllegalArgumentException("unknown errorCode: $errorCode")
        }
    }

    override fun resolveDaggerDependency() {
        DaggerLoadingActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loadingActivityModule(LoadingActivityModule())
                .build()
                .inject(this)
    }
}




















































