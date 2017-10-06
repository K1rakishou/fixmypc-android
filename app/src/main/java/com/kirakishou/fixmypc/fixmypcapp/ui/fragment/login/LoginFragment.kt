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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.LoginActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.LoginActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
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

    override fun initViewModel(): LoginActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(LoginActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_login
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(loginButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onLoginButtonClick() })
    }

    private fun onLoginButtonClick() {

    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
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




























