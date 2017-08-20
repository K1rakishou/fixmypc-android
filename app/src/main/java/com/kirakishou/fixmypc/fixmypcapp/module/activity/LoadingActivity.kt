package com.kirakishou.fixmypc.fixmypcapp.module.activity

import android.animation.AnimatorSet
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.module.shared_preference.AppSharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.module.shared_preference.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.LoadingActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import timber.log.Timber
import javax.inject.Inject

class LoadingActivity : BaseActivity(), LoadingActivityView {

    @Inject
    lateinit var mPresenter: LoadingActivityPresenterImpl

    @Inject
    lateinit var mAppSharedPreferences: AppSharedPreferences

    @Inject
    lateinit var mAppSettings: AppSettings

    lateinit var accountInfoPrefs: AccountInfoPreference

    override fun getContentView(): Int = R.layout.activity_loading
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()
    override fun onInitPresenter() = mPresenter.initPresenter()
    override fun onDestroyPresenter() = mPresenter.destroyPresenter()

    override fun onViewReady() {
        accountInfoPrefs = mAppSharedPreferences.prepare<AccountInfoPreference>()

        //FIXME: accountInfoPrefs should be loaded from preferences via accountInfoPrefs.load()
        //don't forger to delete the following:
        accountInfoPrefs.login = Fickle.of("test@gmail.com")
        accountInfoPrefs.password = Fickle.of("1234567890")

        if (accountInfoPrefs.exists()) {
            mPresenter.startLoggingIn(accountInfoPrefs.login.get(), accountInfoPrefs.password.get())
        } else {
            accountInfoPrefs.clear()
            runGuestMainActivity()
        }
    }

    override fun onViewStop() {

    }

    override fun runGuestMainActivity() {

    }

    override fun runClientMainActivity(sessionId: String, accountType: AccountType) {
        runActivity(ClientMainActivity::class.java, true)
    }

    override fun runSpecialistMainActivity(sessionId: String, accountType: AccountType) {
    }

    override fun onCouldNotConnectToServer(error: Throwable) {
        Timber.e(error)

        //TODO: show reconnection button
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun onUnknownError(error: Throwable) {
        showErrorMessageDialog(error.message!!)
    }

    override fun resolveDaggerDependency() {
        DaggerLoadingActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loadingActivityModule(LoadingActivityModule(this))
                .build()
                .inject(this)
    }
}




















































