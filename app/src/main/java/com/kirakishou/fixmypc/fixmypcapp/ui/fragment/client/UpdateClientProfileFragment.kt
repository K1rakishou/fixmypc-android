package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateClientProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateClientProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.ClientProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateClientProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateClientProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateClientProfileActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class UpdateClientProfileFragment : BaseFragment<UpdateClientProfileActivityViewModel>() {

    @BindView(R.id.profile_name)
    lateinit var profileName: AppCompatEditText

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: AppCompatEditText

    @BindView(R.id.update_profile_info_button)
    lateinit var updateProfileInfoButton: AppCompatButton

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mViewModelFactory: UpdateClientProfileActivityViewModelFactory

    @Inject
    lateinit var mNavigator: UpdateClientProfileActivityNavigator

    override fun initViewModel(): UpdateClientProfileActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(UpdateClientProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_update_client_profile
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += RxTextView.textChanges(profileName)
                .subscribeOn(AndroidSchedulers.mainThread())
                .skip(2)
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
                .subscribe({ updateProfileInfoButton.isEnabled = it })

        mCompositeDisposable += RxTextView.textChanges(profilePhone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .skip(2)
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
                .subscribe({ updateProfileInfoButton.isEnabled = it })

        mCompositeDisposable += RxView.clicks(updateProfileInfoButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileInfoButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateClientProfileFragmentUiInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateClientProfileFragmentUiInfo(it) })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateProfileInfoResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileInfoResponse() })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })
    }

    private fun onUpdateProfileInfoResponse() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
    }

    private fun onUpdateClientProfileFragmentUiInfo(newProfile: ClientProfilePacket) {
        TODO("send broadcast to update client profile UI")
    }

    private fun onUpdateProfileInfoButtonClick() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
        mNavigator.popFragment()

        showToast("Профиль обновлён", Toast.LENGTH_LONG)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)

        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerUpdateClientProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateClientProfileActivityModule(UpdateClientProfileActivityModule(activity as UpdateClientProfileActivity))
                .build()
                .inject(this)
    }
}


























