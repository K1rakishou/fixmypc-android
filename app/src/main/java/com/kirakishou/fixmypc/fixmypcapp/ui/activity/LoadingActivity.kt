package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.annotation.RequiresViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.LoginPasswordDTO
import com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.LoadingActivityViewModelImpl
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

@RequiresViewModel(LoadingActivityViewModelImpl::class)
class LoadingActivity : BaseActivity<LoadingActivityViewModelImpl>() {

    /*@Inject
    lateinit var mPresenter: LoadingActivityPresenterImpl*/

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    @Inject
    lateinit var mAppSettings: AppSettings

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val accountInfoPrefs by lazy { mAppSharedPreference.prepare<AccountInfoPreference>() }

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

    private fun onUnknownError(error: Throwable) {
        showErrorMessageDialog(error.message!!)
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




















































