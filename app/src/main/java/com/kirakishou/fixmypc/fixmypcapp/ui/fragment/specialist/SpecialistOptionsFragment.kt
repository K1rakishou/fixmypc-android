package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication

import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import android.content.Intent
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.LoadingActivity


class SpecialistOptionsFragment : BaseFragment<SpecialistMainActivityViewModel>() {

    @BindView(R.id.logout_button)
    lateinit var logoutButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    private val accountInfoPrefs by lazy { mAppSharedPreference.prepare<AccountInfoPreference>() }

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_specialist_options
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        accountInfoPrefs.load()

        initRx()
    }

    override fun onPause() {
        super.onPause()

        accountInfoPrefs.save()
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(logoutButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onLogoutButtonClick() })
    }

    private fun onLogoutButtonClick() {
        accountInfoPrefs.clear()

        val intent = Intent(activity, LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(activity as SpecialistMainActivity))
                .build()
                .inject(this)
    }
}
