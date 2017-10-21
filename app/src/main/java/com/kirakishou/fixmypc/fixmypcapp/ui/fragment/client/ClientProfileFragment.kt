package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.ClientProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateClientProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateSpecialistProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientMainActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ClientProfileFragment : BaseFragment<ClientMainActivityViewModel>() {

    @BindView(R.id.profile_name)
    lateinit var profileName: TextView

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: TextView

    @BindView(R.id.profile_update_button)
    lateinit var profileUpdateButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    @Inject
    lateinit var mRefWatcher: RefWatcher

    @Inject
    lateinit var mNavigator: ClientMainActivityNavigator

    private val receiver = UpdateClientProfileUiCommandReceiver()
    private var savedProfile: ClientProfile? = null

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_client_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
        getClientProfile()

        activity.registerReceiver(receiver,
                IntentFilter(Constant.ReceiverActions.UPDATE_CLIENT_PROFILE_UI_NOTIFICATION))
    }

    override fun onFragmentViewDestroy() {
        activity.unregisterReceiver(receiver)
        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(profileUpdateButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onProfileUpdateButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onGetClientProfileResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetClientProfileResponse)
    }

    private fun getClientProfile() {
        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.CLIENT_PROFILE)
        getViewModel().mInputs.getClientProfile()
    }

    private fun onProfileUpdateButtonClick() {
        if (savedProfile == null) {
            Timber.e("savedProfile must not be null")
            return
        }

        savedProfile!!.let { profile ->
            val args = Bundle()
            args.putLong("user_id", profile.userId)
            args.putString("name", profile.name)
            args.putString("phone", profile.phone)

            runActivityWithArgs(UpdateClientProfileActivity::class.java, args)
        }
    }

    private fun onGetClientProfileResponse(profile: ClientProfile) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.CLIENT_PROFILE)
        savedProfile = profile

        if (profile.name.isNotEmpty()) {
            profileName.text = profile.name
        } else {
            profileName.text = "Не заполнено"
        }

        if (profile.phone.isNotEmpty()) {
            profilePhone.text = profile.phone
        } else {
            profilePhone.text = "Не заполнено"
        }
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.CLIENT_PROFILE)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.CLIENT_PROFILE)

        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(activity as ClientMainActivity))
                .build()
                .inject(this)
    }

    inner class UpdateClientProfileUiCommandReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constant.ReceiverActions.UPDATE_CLIENT_PROFILE_UI_NOTIFICATION) {
                Timber.d("broadcast with action UPDATE_CLIENT_PROFILE_UI_NOTIFICATION received")

                val args = intent.extras
                val newName = args.getString("new_name")
                val newPhone = "Телефон: ${args.getString("new_phone")}"

                profileName.text = newName
                profilePhone.text = newPhone

                savedProfile!!.name = newName
                savedProfile!!.phone = newPhone
            }
        }
    }
}






























