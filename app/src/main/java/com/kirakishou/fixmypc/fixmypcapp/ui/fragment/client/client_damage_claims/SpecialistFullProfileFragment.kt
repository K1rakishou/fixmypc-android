package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.client_damage_claims


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
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class SpecialistFullProfileFragment : BaseFragment<ClientMainActivityViewModel>() {

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
    lateinit var mViewModelFactory: ClientMainActivityViewModelFactory

    private var specialistProfileFickle = Fickle.empty<SpecialistProfile>()

    override fun initViewModel(): ClientMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_specialist_full_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initSpecialistProfile()
        initRx()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(profileRepairHistoryButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onShowRepairHistoryButtonClick() })

        mCompositeDisposable += RxView.clicks(profileAssignSpecialistButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAssignSpecialistButtonClick() })
    }

    private fun onAssignSpecialistButtonClick() {
        Timber.e("Assign specialist")
    }

    private fun onShowRepairHistoryButtonClick() {
        Timber.e("Show repair history")
    }

    private fun initSpecialistProfile() {
        val bundle = arguments
        if (bundle == null) {
            throw IllegalArgumentException("no fragment arguments")
        }

        specialistProfileFickle = Fickle.of(SpecialistProfile(
                bundle.getLong("user_id"),
                bundle.getString("name"),
                bundle.getFloat("rating"),
                bundle.getString("photo_name"),
                bundle.getLong("registered_on,"),
                bundle.getInt("success_repairs"),
                bundle.getInt("fail_repairs")))

        if (specialistProfileFickle.isPresent()) {
            val profile = specialistProfileFickle.get()

            profileName.text = profile.name
            profileRating.rating = profile.rating

            val registeredOnText = TimeUtils.format(profile.registeredOn)
            profileRegisteredOn.text = "Зарегистрирован с $registeredOnText"

            val totalRepairs = profile.successRepairs + profile.failRepairs
            profileTotalRepairs.text = "Всего проведено ремонтов: $totalRepairs"

            profileSuccessRepairs.text = "Успешных ремонтов: ${profile.successRepairs}"
            profileFailRepairs.text = "Неудачных ремонтов: ${profile.failRepairs}"
        }
    }

    override fun onBadResponse(errorCode: ErrorCode.Remote) {
        val message = ErrorMessage.getRemoteErrorMessage(activity, errorCode)
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientMainActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientMainActivityModule(ClientMainActivityModule(activity as ClientMainActivity))
                .build()
                .inject(this)
    }
}
