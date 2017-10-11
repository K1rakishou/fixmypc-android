package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatRatingBar
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateSpecialistProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class SpecialistProfileFragment : BaseFragment<SpecialistMainActivityViewModel>() {

    @BindView(R.id.profile_photo)
    lateinit var profilePhoto: ImageView

    @BindView(R.id.profile_name)
    lateinit var profileName: TextView

    @BindView(R.id.profile_rating)
    lateinit var profileRating: AppCompatRatingBar

    @BindView(R.id.profile_registered_on)
    lateinit var profileRegisteredOn: TextView

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: TextView

    @BindView(R.id.profile_total_repairs)
    lateinit var profileTotalRepairs: TextView

    @BindView(R.id.profile_success_repairs)
    lateinit var profileSuccessRepairs: TextView

    @BindView(R.id.profile_fail_repairs)
    lateinit var profileFailRepairs: TextView

    @BindView(R.id.profile_update_button)
    lateinit var updateProfileButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val receiver = UpdateWaitingReceiver()
    private val fragmentTag = Constant.FragmentTags.SPECIALIST_PROFILE
    private var savedProfile: SpecialistProfile? = null

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_specialist_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()

        if (savedInstanceState == null) {
            mNavigator.showLoadingIndicatorFragment()
            getViewModel().mInputs.getSpecialistProfile()
        }

        activity.registerReceiver(receiver, IntentFilter(Constant.ReceiverActions.WAIT_FOR_SPECIALIST_PROFILE_UPDATE_NOTIFICATION))
    }

    override fun onFragmentViewDestroy() {
        activity.unregisterReceiver(receiver)

        mRefWatcher.watch(this)
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(updateProfileButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onSpecialistProfileResponseSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSpecialistProfileResponseSubject(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })
    }

    private fun onUpdateProfileButtonClick() {
        if (savedProfile == null) {
            Timber.e("savedProfile must not be null")
            return
        }

        savedProfile!!.let { profile ->
            val args = Bundle()
            args.putLong("user_id", profile.userId)
            args.putString("name", profile.name)
            args.putFloat("rating", profile.rating)
            args.putString("photo_name", profile.photoName)
            args.putString("phone", profile.phone)
            args.putLong("registered_on", profile.registeredOn)
            args.putInt("success_repairs", profile.successRepairs)
            args.putInt("fail_repairs", profile.failRepairs)
            args.putBoolean("is_filled_in", profile.isFilledIn)

            runActivityWithArgs(UpdateSpecialistProfileActivity::class.java, args)
        }
    }

    private fun onSpecialistProfileResponseSubject(profile: SpecialistProfile) {
        mNavigator.hideLoadingIndicatorFragment()

        updateUi(profile)
    }

    private fun updateUi(profile: SpecialistProfile) {
        savedProfile = profile

        loadPhoto(profile.photoName, profile.userId)
        profileName.text = profile.name
        profileRating.rating = profile.rating

        val registeredOnFormatted = TimeUtils.format(profile.registeredOn)
        profileRegisteredOn.text = "Зарегистрирован с $registeredOnFormatted"

        profilePhone.text = "Телефон: ${profile.phone}"
        profileTotalRepairs.text = "Всего проведено ремонтов: ${profile.failRepairs + profile.successRepairs}"
        profileSuccessRepairs.text = "Успешных ремонтов: ${profile.successRepairs}"
        profileFailRepairs.text = "Неудачных ремонтов: ${profile.failRepairs}"
    }

    private fun loadPhoto(photoName: String, userId: Long) {
        if (photoName.isNotEmpty()) {
            mImageLoader.loadProfileImageFromNetInto(userId, photoName, profilePhoto)
        } else {
            //TODO: load default profile image
        }
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment()

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment()
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerSpecialistMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .specialistMainActivityModule(SpecialistMainActivityModule(activity as SpecialistMainActivity))
                .build()
                .inject(this)
    }

    inner class UpdateWaitingReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constant.ReceiverActions.WAIT_FOR_SPECIALIST_PROFILE_UPDATE_NOTIFICATION) {
               activity.runOnUiThread {
                   val args = intent.extras

                   val updateType = args.getString("update_type")
                   when (updateType) {
                       "photo" -> {
                           loadPhoto(args.getString("new_photo_name"), savedProfile!!.userId)
                       }
                       "info" -> {
                           profileName.text = args.getString("new_name")
                           profilePhone.text = "Телефон: ${args.getString("new_phone")}"
                       }
                       else -> throw IllegalStateException("Unknown updateType: $updateType")
                   }
               }
            }
        }
    }
}
