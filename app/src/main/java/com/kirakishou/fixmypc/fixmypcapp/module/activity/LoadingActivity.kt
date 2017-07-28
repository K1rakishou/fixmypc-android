package com.kirakishou.fixmypc.fixmypcapp.module.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoadingActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoadingActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.LoadingActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.AppSharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.AppSharedPreferences.SharedPreferenceType.AccountInfo
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.preference.AccountInfoPreference
import javax.inject.Inject

class LoadingActivity : BaseActivity(), LoadingActivityView {

    @Inject
    lateinit var mPresenter: LoadingActivityPresenterImpl

    @Inject
    lateinit var mAppSharedPreferences: AppSharedPreferences

    @Inject
    lateinit var mAppSettings: AppSettings

    override fun getContentView(): Int = R.layout.activity_loading

    override fun loadStartAnimations(): AnimatorSet {
        return AnimatorSet()
    }

    override fun loadExitAnimations(): AnimatorSet {
        return AnimatorSet()
    }

    override fun onPrepareView(savedInstanceState: Bundle?, intent: Intent) {
        super.onPrepareView(savedInstanceState, intent)
    }

    override fun onViewReady() {
        mPresenter.initPresenter()

        val accountInfoPrefs = mAppSharedPreferences.get<AccountInfoPreference>(AccountInfo)

        accountInfoPrefs.login = Fickle.of("test2@gmail.com")
        accountInfoPrefs.password = Fickle.of("1234567890")

        //FIXME:
        //accountInfoPrefs.load()

        if (accountInfoPrefs.exists()) {
            mPresenter.startLoggingIn(accountInfoPrefs)
        } else {
            accountInfoPrefs.clear()
            runChooseCategoryActivity()
        }
    }

    override fun onLoggedIn(loginResponse: LoginResponse) {
        mAppSettings.sessionId = Fickle.of(loginResponse.sessionId)
        mAppSettings.accountType = Fickle.of(AccountType.from(loginResponse.accountType))

        when (mAppSettings.accountType.get()) {
            AccountType.Client -> {
                runChooseCategoryActivity()
            }

            AccountType.Specialist -> {
                runSpecialistMainActivity()
            }

            AccountType.Guest -> {
                //should not happen
                throw IllegalStateException("Server returned accountType.Guest")
            }
        }
    }

    private fun runChooseCategoryActivity() {

    }

    private fun runSpecialistMainActivity() {

    }

    override fun onViewStop() {
        mPresenter.destroyPresenter()
    }

    override fun resolveDaggerDependency() {
        DaggerLoadingActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loadingActivityModule(LoadingActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun onServerError(statusCode: StatusCode) {
        when (statusCode) {
            StatusCode.STATUS_WRONG_LOGIN_OR_PASSWORD -> runChooseCategoryActivity()

            else -> {
                val message = ErrorMessage.getErrorMessage(this, statusCode)
                showToast(message, Toast.LENGTH_LONG)
            }
        }
    }

    override fun onUnknownError(error: Throwable) {
        showErrorMessageDialog(error.message!!)
    }
}




















































