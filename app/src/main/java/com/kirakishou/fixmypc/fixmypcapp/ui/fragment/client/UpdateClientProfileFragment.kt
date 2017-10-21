package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.ClientProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.ClientProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateClientProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateClientProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateClientProfileActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
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

    private var savedProfile: ClientProfile? = null

    override fun initViewModel(): UpdateClientProfileActivityViewModel? {
        return ViewModelProviders.of(this, mViewModelFactory).get(UpdateClientProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_update_client_profile
    override fun loadStartAnimations(): AnimatorSet = AnimatorSet()
    override fun loadExitAnimations(): AnimatorSet = AnimatorSet()

    private fun getProfileFromBundle(arguments: Bundle?) {
        if (arguments != null) {
            val profile = ClientProfile()
            profile.userId = arguments.getLong("user_id")
            profile.name = arguments.getString("name")
            profile.phone = arguments.getString("phone")

            profileName.setText(profile.name)
            profilePhone.setText(profile.phone)

            savedProfile = profile
        } else {
            Timber.e("fragment must have arguments")
        }
    }

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()

        getProfileFromBundle(arguments)
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += RxTextView.textChanges(profileName)
                .observeOn(AndroidSchedulers.mainThread())
                .skip(2)
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
                .subscribe({ updateProfileInfoButton.isEnabled = it })

        mCompositeDisposable += RxTextView.textChanges(profilePhone)
                .observeOn(AndroidSchedulers.mainThread())
                .skip(2)
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
                .subscribe({ updateProfileInfoButton.isEnabled = it })

        mCompositeDisposable += RxView.clicks(updateProfileInfoButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileInfoButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateClientProfileFragmentUiInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateClientProfileFragmentUiInfo(it) })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateProfileInfoResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileInfoResponse() })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })
    }

    private fun onUpdateProfileInfoButtonClick() {
        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_CLIENT_PROFILE)

        val newName = profileName.text.toString()
        val newPhone = profilePhone.text.toString()

        getViewModel().mInputs.updateProfileInfoSubject(newName, newPhone)
    }

    private fun onUpdateProfileInfoResponse() {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_CLIENT_PROFILE)
        finishActivity()
    }

    private fun onUpdateClientProfileFragmentUiInfo(newProfile: ClientProfilePacket) {
        val intent = Intent()
        intent.action = Constant.ReceiverActions.UPDATE_CLIENT_PROFILE_UI_NOTIFICATION

        val args = Bundle()
        args.putString("new_name", newProfile.profileName)
        args.putString("new_phone", newProfile.profilePhone)
        intent.putExtras(args)

        sendBroadcast(intent)
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_CLIENT_PROFILE)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_CLIENT_PROFILE)

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


























