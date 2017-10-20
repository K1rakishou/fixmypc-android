package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.specialist


import android.Manifest
import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kirakishou.billboards.modules.controller.PhotoView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateSpecialistProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateSpecialistProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.hideKeyboard
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateSpecialistProfileActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.UpdateSpecialistProfileActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.UpdateSpecialistProfileActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.PermissionGrantedCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.RequestPermissionCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.UpdateSpecialistProfileActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class UpdateSpecialistProfileFragment : BaseFragment<UpdateSpecialistProfileActivityViewModel>(),
        PhotoView.OnPhotoClickedListener,
        PermissionGrantedCallback {

    @BindView(R.id.profile_photo)
    lateinit var profilePhoto: PhotoView

    @BindView(R.id.update_profile_photo_button)
    lateinit var updateProfilePhotoButton: AppCompatButton

    @BindView(R.id.profile_name)
    lateinit var profileName: AppCompatEditText

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: AppCompatEditText

    @BindView(R.id.update_profile_info_button)
    lateinit var updateProfileInfoButton: AppCompatButton

    @Inject
    lateinit var mNavigator: UpdateSpecialistProfileActivityNavigator

    @Inject
    lateinit var mViewModelFactory: UpdateSpecialistProfileActivityViewModelFactory

    @Inject
    lateinit var mImageLoader: ImageLoader

    private var savedProfile: SpecialistProfile? = null

    override fun initViewModel(): UpdateSpecialistProfileActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(UpdateSpecialistProfileActivityViewModel::class.java)
    }

    override fun getContentView(): Int = R.layout.fragment_update_specialist_profile
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    private fun getProfileFromBundle(arguments: Bundle?) {
        if (arguments != null) {
            val profile = SpecialistProfile()
            profile.userId = arguments.getLong("user_id")
            profile.name = arguments.getString("name")
            profile.rating = arguments.getFloat("rating")
            profile.photoName = arguments.getString("photo_name")
            profile.phone = arguments.getString("phone")
            profile.registeredOn = arguments.getLong("registered_on")
            profile.successRepairs = arguments.getInt("success_repairs")
            profile.failRepairs = arguments.getInt("fail_repairs")

            profileName.setText(profile.name)
            profilePhone.setText(profile.phone)
            loadPhoto(profile.photoName, profile.userId)

            savedProfile = profile
        } else {
            Timber.e("fragment must have arguments")
        }
    }

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()

        profilePhoto.setImageLoader(mImageLoader)
        profilePhoto.setAddButtonIcon(R.drawable.ic_add_photo)
        profilePhoto.setRemoveButtonIcon(R.drawable.ic_remove_photo)
        profilePhoto.setBorderDrawable(R.drawable.view_border)
        profilePhoto.setOnPhotoClickedCallback(this)

        getProfileFromBundle(arguments)
    }

    override fun onFragmentViewDestroy() {
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
                .subscribe({ onUpdateProfileButtonInfoClick() })

        mCompositeDisposable += RxView.clicks(updateProfilePhotoButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateProfilePhotoButtonClick() })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateSpecialistProfileResponseSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateSpecialistProfileResponse(it) })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateSpecialistProfileFragmentUiInfo()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateSpecialistProfileInfoFragment(it) })

        mCompositeDisposable += getViewModel().mOutputs.onUpdateSpecialistProfileFragmentPhoto()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateSpecialistProfilePhotoFragment(it) })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onBadResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadResponse(it) })
    }

    private fun onUpdateSpecialistProfilePhotoFragment(newPhotoName: String) {
        activity.runOnUiThread {
            val intent = Intent()
            intent.action = Constant.ReceiverActions.UPDATE_SPECIALIST_PROFILE_UI_NOTIFICATION

            val args = Bundle()
            args.putString("update_type", "photo")
            args.putString("new_photo_name", newPhotoName)
            intent.putExtras(args)

            sendBroadcast(intent)
        }
    }

    private fun onUpdateSpecialistProfileInfoFragment(newProfileInfo: NewProfileInfo) {
        activity.runOnUiThread {
            val intent = Intent()
            intent.action = Constant.ReceiverActions.UPDATE_SPECIALIST_PROFILE_UI_NOTIFICATION

            val args = Bundle()
            args.putString("update_type", "info")
            args.putString("new_name", newProfileInfo.name)
            args.putString("new_phone", newProfileInfo.phone)
            intent.putExtras(args)

            sendBroadcast(intent)
        }
    }

    private fun loadPhoto(photoName: String, userId: Long) {
        if (photoName.isNotEmpty()) {
            profilePhoto.loadImageFromNet(photoName, userId)
        } else {
            //TODO: load default profile image
        }
    }

    private fun onUpdateProfilePhotoButtonClick() {
        if (profilePhoto.imageFile == null) {
            showToast("Необходимо выбрать изображение для профиля", Toast.LENGTH_SHORT)
            return
        }

        val imageFile = profilePhoto.imageFile!!

        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
        getViewModel().mInputs.updateSpecialistProfilePhoto(imageFile.absolutePath)

        hideKeyboard()
    }

    private fun onUpdateProfileButtonInfoClick() {
        profileName.error = null

        if (profileName.text.isEmpty()) {
            profileName.error = "Необходимо указать имя"
            return
        }

        if (profilePhone.text.isEmpty()) {
            profileName.error = "Необходимо указать телефон"
            return
        }

        val name = profileName.text.toString()
        val phone = profilePhone.text.toString()

        mNavigator.showLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
        getViewModel().mInputs.updateSpecialistProfileInfo(name, phone)

        hideKeyboard()
    }

    private fun onUpdateSpecialistProfileResponse(profileUpdate: UpdateSpecialistProfileActivityViewModel.ProfileUpdate) {
        when (profileUpdate) {
            UpdateSpecialistProfileActivityViewModel.ProfileUpdate.ProfileInfo -> {
                showToast("Профиль обновлён", Toast.LENGTH_LONG)
            }

            UpdateSpecialistProfileActivityViewModel.ProfileUpdate.ProfilePhoto -> {
                showToast("Фото профиля обновлено", Toast.LENGTH_LONG)
            }
        }

        mNavigator.hideLoadingIndicatorFragment(Constant.FragmentTags.UPDATE_SPECIALIST_PROFILE)
    }

    override fun addPhoto() {
        val activityHolder = activity as RequestPermissionCallback
        activityHolder.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constant.PermissionCodes.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun removePhoto() {
        profilePhoto.setImageBitmap(null)
        updateProfilePhotoButton.isEnabled = false
    }

    override fun onPermissionGranted() {
        if (!isVisible) {
            return
        }

        EasyImage.openGallery(this, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this@UpdateSpecialistProfileFragment.activity, object : DefaultCallback() {
                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource?, type: Int) {
                    Timber.e(e)
                }

                override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    if (imageFiles.isNotEmpty()) {
                        onImage(imageFiles.first())
                    }
                }

            })
        }
    }

    private fun onImage(imageFile: File) {
        profilePhoto.loadImageFromDisk(imageFile)
        updateProfilePhotoButton.isEnabled = true
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
        DaggerUpdateSpecialistProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateSpecialistProfileActivityModule(UpdateSpecialistProfileActivityModule(activity as UpdateSpecialistProfileActivity))
                .build()
                .inject(this)
    }
}





























