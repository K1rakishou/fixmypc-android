package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.update_specialist_profile


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
import com.kirakishou.billboards.modules.controller.PhotoView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerUpdateSpecialistProfileActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.UpdateSpecialistProfileActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
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

    @BindView(R.id.profile_name)
    lateinit var profileName: AppCompatEditText

    @BindView(R.id.profile_phone)
    lateinit var profilePhone: AppCompatEditText

    @BindView(R.id.update_profile_button)
    lateinit var updateProfileButton: AppCompatButton

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
            val profile  = SpecialistProfile()
            profile.userId = arguments.getLong("user_id")
            profile.name = arguments.getString("name")
            profile.rating = arguments.getFloat("rating")
            profile.photoName = arguments.getString("photo_name")
            profile.phone = arguments.getString("phone")
            profile.registeredOn = arguments.getLong("registered_on")
            profile.successRepairs = arguments.getInt("success_repairs")
            profile.failRepairs = arguments.getInt("fail_repairs")
            profile.isFilledIn = arguments.getBoolean("is_filled_in")

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
        getProfileFromBundle(arguments)

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

        mCompositeDisposable += getViewModel().mOutputs.onUpdateSpecialistProfileResponseSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUpdateSpecialistProfileResponse() })
    }

    private fun onUpdateSpecialistProfileFragment(newProfileInfo: NewProfileInfo) {
        activity.runOnUiThread {
            profileName.setText(newProfileInfo.name)
            profilePhone.setText("Телефон: ${newProfileInfo.phone}")

            //TODO
            //loadPhoto(newProfileInfo.photoPath, save)
        }
    }

    private fun loadPhoto(photoName: String, userId: Long) {
        if (photoName.isNotEmpty()) {
            mImageLoader.loadProfileImageFromNetInto(userId, photoName, profilePhoto)
        } else {
            //TODO: load default profile image
        }
    }

    private fun onUpdateProfileButtonClick() {
        profileName.error = null

        if (profilePhoto.imageFile == null) {
            showToast("Необходимо выбрать изображение для профиля", Toast.LENGTH_SHORT)
            return
        }

        if (profileName.text.isEmpty()) {
            profileName.error = "Необходимо указать имя"
            return
        }

        if (profilePhone.text.isEmpty()) {
            profileName.error = "Необходимо указать телефон"
            return
        }

        val imageFile = profilePhoto.imageFile!!
        val name = profileName.text.toString()
        val phone = profilePhone.text.toString()

        mNavigator.showLoadingIndicatorFragment()
        getViewModel().mInputs.updateSpecialistProfile(imageFile.absolutePath, name, phone)
    }

    private fun onUpdateSpecialistProfileResponse() {
        mNavigator.hideLoadingIndicatorFragment()
    }

    override fun addPhoto() {
        val activityHolder = activity as RequestPermissionCallback
        activityHolder.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constant.PermissionCodes.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun removePhoto() {
        profilePhoto.setImageBitmap(null)
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

                override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                    super.onCanceled(source, type)
                }
            })
        }
    }

    private fun onImage(imageFile: File) {
        profilePhoto.loadImageFromDisk(imageFile)
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
        DaggerUpdateSpecialistProfileActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .updateSpecialistProfileActivityModule(UpdateSpecialistProfileActivityModule(activity as UpdateSpecialistProfileActivity))
                .build()
                .inject(this)
    }
}





























