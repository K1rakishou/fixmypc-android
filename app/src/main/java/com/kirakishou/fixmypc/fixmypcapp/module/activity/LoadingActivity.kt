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
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.Fickle
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

        accountInfoPrefs.login = Fickle.of("test@gmail.com")
        accountInfoPrefs.password = Fickle.of("1234567890")

        //accountInfoPrefs.load()

        if (accountInfoPrefs.exists()) {
            mPresenter.startLoggingIn(accountInfoPrefs)
        } else {
            runChooseCategoryActivity()
        }
    }

    private fun runChooseCategoryActivity() {

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

    override fun onUnknownError(error: Throwable) {
        showToast(error.localizedMessage, Toast.LENGTH_LONG)
    }
}




















































