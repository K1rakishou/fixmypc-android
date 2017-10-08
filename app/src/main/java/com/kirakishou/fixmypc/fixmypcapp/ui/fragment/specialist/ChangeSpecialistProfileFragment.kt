package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.billboards.modules.controller.PhotoView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerSpecialistMainActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.SpecialistMainActivityModule
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorMessage
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.SpecialistMainActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.SpecialistMainActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.SpecialistMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.SpecialistMainActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


class ChangeSpecialistProfileFragment : BaseFragment<SpecialistMainActivityViewModel>(), PhotoView.OnPhotoClickedListener {

    @BindView(R.id.profile_photo)
    lateinit var profilePhoto: PhotoView

    @BindView(R.id.profile_name)
    lateinit var profileName: AppCompatEditText

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: AppCompatEditText

    @BindView(R.id.update_profile_button)
    lateinit var updateProfileButton: AppCompatButton

    @Inject
    lateinit var mNavigator: SpecialistMainActivityNavigator

    @Inject
    lateinit var mViewModelFactory: SpecialistMainActivityViewModelFactory

    override fun initViewModel(): SpecialistMainActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(SpecialistMainActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_change_specialist_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()

        profilePhoto.setAddButtonIcon(R.drawable.ic_add_photo)
        profilePhoto.setRemoveButtonIcon(R.drawable.ic_remove_photo)
        profilePhoto.setBorderDrawable(R.drawable.view_border)
        profilePhoto.setOnPhotoClickedCallback(this)
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(updateProfileButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfileButtonClick() })
    }

    override fun addPhoto() {

    }

    override fun removePhoto() {

    }

    private fun onUpdateProfileButtonClick() {
        mNavigator.showLoadingIndicatorFragment()


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
