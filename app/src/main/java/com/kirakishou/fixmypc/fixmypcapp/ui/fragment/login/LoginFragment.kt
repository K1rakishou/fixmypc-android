package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.login


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerLoginActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.LoginActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.removeSpaces
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AccountInfoPreference
import com.kirakishou.fixmypc.fixmypcapp.helper.preference.AppSharedPreference
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginResponseDataDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoginActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoginActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.LoginActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.LoginActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginActivityViewModel>() {

    @BindView(R.id.input_login)
    lateinit var inputLogin: AppCompatEditText

    @BindView(R.id.input_password)
    lateinit var inputPassword: AppCompatEditText

    @BindView(R.id.button_login)
    lateinit var loginButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: LoginActivityViewModelFactory

    @Inject
    lateinit var mAppSharedPreference: AppSharedPreference

    @Inject
    lateinit var mNavigator: LoginActivityNavigator

    private val accountInfoPrefs by lazy { mAppSharedPreference.prepare<AccountInfoPreference>() }

    override fun initViewModel(): LoginActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(LoginActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_login
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onPause() {
        super.onPause()
        accountInfoPrefs.save()
    }

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        accountInfoPrefs.load()
        initRx()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(loginButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map {
                    val login = inputLogin.text.toString().removeSpaces()
                    val password = inputPassword.text.toString().removeSpaces()

                    return@map LoginPasswordDTO(login, password)
                }
                .subscribe({ onLoginButtonClick(it) })

        mCompositeDisposable += getViewModel().mOutputs.runClientMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runClientMainActivity(it) })

        mCompositeDisposable += getViewModel().mOutputs.runSpecialistMainActivity()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ runSpecialistMainActivity(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    private fun onLoginButtonClick(loginPassword: LoginPasswordDTO) {
        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.LOGIN_FRAGMENT)
        getViewModel().mInputs.startLoggingIn(loginPassword)
    }

    private fun runClientMainActivity(loginResponseData: LoginResponseDataDTO) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.LOGIN_FRAGMENT)

        updateAccountInfoPres(loginResponseData.login, loginResponseData.password)
        runActivity(ClientMainActivity::class.java, true)
    }

    private fun runSpecialistMainActivity(loginResponseData: LoginResponseDataDTO) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.LOGIN_FRAGMENT)

        updateAccountInfoPres(loginResponseData.login, loginResponseData.password)
        runActivity(SpecialistMainActivity::class.java, true)
    }

    private fun updateAccountInfoPres(login: String, password: String) {
        accountInfoPrefs.login = Fickle.of(login)
        accountInfoPrefs.password = Fickle.of(password)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.LOGIN_FRAGMENT)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.LOGIN_FRAGMENT)

        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerLoginActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .loginActivityModule(LoginActivityModule(activity as LoginActivity))
                .build()
                .inject(this)
    }
}




























