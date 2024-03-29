package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.new_damage_claim

import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.CardView
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientNewDamageClaimActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.hideKeyboard
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject


class DamageClaimDescriptionFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>() {

    @BindView(R.id.damage_claim_description)
    lateinit var mDamageClaimDescriptionEditText: TextInputEditText

    @BindView(R.id.button_load_next)
    lateinit var mButtonLoadNext: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    @Inject
    lateinit var mRefWatcher: RefWatcher

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_damage_claim_description
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mButtonLoadNext)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionDescription(mDamageClaimDescriptionEditText.text.toString())
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment() {
        hideKeyboard()
        mNavigator.navigateToDamageClaimLocationFragment()
    }

    private fun setMalfunctionDescription(description: String) {
        getViewModel().setDescription(description)
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }
}
