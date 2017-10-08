package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
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
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
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

    private val fragmentTag = Constant.FragmentTags.SPECIALIST_PROFILE

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
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(updateProfileButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onSpecialistProfileResponseSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter { getViewModel().currentFragmentTag == fragmentTag }
                .subscribe({ onSpecialistProfileResponseSubject(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter { getViewModel().currentFragmentTag == fragmentTag }
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter { getViewModel().currentFragmentTag == fragmentTag }
                .subscribe({ onBadResponse(it) })
    }

    private fun onUpdateProfileButtonClick() {
        mNavigator.navigateToUpdateSpecialistProfileFragment()
    }

    private fun onSpecialistProfileResponseSubject(profile: SpecialistProfile) {
        mNavigator.hideLoadingIndicatorFragment()

        updateUi(profile)
    }

    private fun updateUi(profile: SpecialistProfile) {
        mImageLoader.loadProfileImageFromNetInto() //TODO

        profileName.text = profile.name
        profileRating.rating = profile.rating

        val registeredOnFormatted = TimeUtils.format(profile.registeredOn)
        profileRegisteredOn.text = "Зарегистрирован с $registeredOnFormatted"

        profilePhone.text = "Телефон: " // TODO
        profileTotalRepairs.text = "Всего проведено ремонтов: ${profile.failRepairs + profile.successRepairs}"
        profileSuccessRepairs.text = "Успешных ремонтов: ${profile.successRepairs}"
        profileFailRepairs.text = "Неудачных ремонтов: ${profile.failRepairs}"
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

}
