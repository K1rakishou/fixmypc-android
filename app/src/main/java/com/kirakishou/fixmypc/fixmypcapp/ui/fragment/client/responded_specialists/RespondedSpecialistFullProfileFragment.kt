package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.responded_specialists


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerRespondedSpecialistsActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.RespondedSpecialistsActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.AssignSpecialistResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.RespondedSpecialistsViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.RespondedSpecialistsActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.RespondedSpecialistsActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.RespondedSpecialistsActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class RespondedSpecialistFullProfileFragment : BaseFragment<RespondedSpecialistsViewModel>() {

    @BindView(R.id.profile_photo)
    lateinit var profilePhoto: ImageView

    @BindView(R.id.profile_assign_specialist_button)
    lateinit var profileAssignSpecialistButton: FloatingActionButton

    @BindView(R.id.profile_name)
    lateinit var profileName: TextView

    @BindView(R.id.profile_rating)
    lateinit var profileRating: AppCompatRatingBar

    @BindView(R.id.profile_registered_on)
    lateinit var profileRegisteredOn: TextView

    @BindView(R.id.profile_total_repairs)
    lateinit var profileTotalRepairs: TextView

    @BindView(R.id.profile_success_repairs)
    lateinit var profileSuccessRepairs: TextView

    @BindView(R.id.profile_fail_repairs)
    lateinit var profileFailRepairs: TextView

    @BindView(R.id.profile_repair_history_button)
    lateinit var profileRepairHistoryButton: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: RespondedSpecialistsActivityViewModelFactory

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mNavigator: RespondedSpecialistsActivityNavigator

    private var mSpecialistProfileFickle = Fickle.empty<SpecialistProfile>()
    private var mDamageClaimId = -1L

    override fun initViewModel(): RespondedSpecialistsViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(RespondedSpecialistsViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_responded_specialist_full_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        updateSpecialistProfileUi()
        initRx()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun updateSpecialistProfileUi() {
        val bundle = arguments
        if (bundle == null) {
            throw IllegalArgumentException("no fragment arguments")
        }

        mSpecialistProfileFickle = Fickle.of(SpecialistProfile(
                bundle.getLong("user_id"),
                bundle.getString("name"),
                bundle.getFloat("rating"),
                bundle.getString("photo_name"),
                bundle.getString("phone"),
                bundle.getLong("registered_on,"),
                bundle.getInt("success_repairs"),
                bundle.getInt("fail_repairs")))

        mDamageClaimId = bundle.getLong("damage_claim_id", -1L)

        if (mSpecialistProfileFickle.isPresent()) {
            val profile = mSpecialistProfileFickle.get()

            profileName.text = profile.name
            profileRating.rating = profile.rating

            val registeredOnText = TimeUtils.format(profile.registeredOn)
            profileRegisteredOn.text = "Зарегистрирован с $registeredOnText"

            val totalRepairs = profile.successRepairs + profile.failRepairs
            profileTotalRepairs.text = "Всего проведено ремонтов: $totalRepairs"

            profileSuccessRepairs.text = "Успешных ремонтов: ${profile.successRepairs}"
            profileFailRepairs.text = "Неудачных ремонтов: ${profile.failRepairs}"

            mImageLoader.loadProfileImageFromNetInto(profile.userId, profile.photoName, profilePhoto)
        }
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(profileRepairHistoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onShowRepairHistoryButtonClick() })

        mCompositeDisposable += RxView.clicks(profileAssignSpecialistButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAssignSpecialistButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onAssignSpecialistResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAssignSpecialistResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })
    }

    private fun onAssignSpecialistButtonClick() {
        check(mDamageClaimId != -1L)

        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.RESPONDED_SPECIALIST_FULL_PROFILE)
        val profile = mSpecialistProfileFickle.get()
        getViewModel().mInputs.assignSpecialist(profile.userId, mDamageClaimId)
    }

    private fun onShowRepairHistoryButtonClick() {
        Timber.e("Show repair history")
    }

    private fun onAssignSpecialistResponse(response: AssignSpecialistResponse) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.RESPONDED_SPECIALIST_FULL_PROFILE)
        Timber.e("response errorCode: ${response.errorCode}")
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.RESPONDED_SPECIALIST_FULL_PROFILE)

        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.RESPONDED_SPECIALIST_FULL_PROFILE)

        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerRespondedSpecialistsActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .respondedSpecialistsActivityModule(RespondedSpecialistsActivityModule(activity as RespondedSpecialistsActivity))
                .build()
                .inject(this)
    }
}
