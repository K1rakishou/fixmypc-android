package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentedActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewMalfunctionActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity.ClientNewMalfunctionActivityPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.ClientNewMalfunctionActivityView
import com.kirakishou.fixmypc.fixmypcapp.ui.dialog.ProgressDialog
import com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction.*
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject


class ClientNewMalfunctionActivity : BaseFragmentedActivity(), ClientNewMalfunctionActivityView, ClientNewMalfunctionActivityFragmentCallback {

    @Inject
    lateinit var mActivityPresenter: ClientNewMalfunctionActivityPresenterImpl

    @Inject
    lateinit var mPermissionManager: PermissionManager

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val malfunctionRequestInfo = DamageClaimInfo()
    private lateinit var progressDialog: ProgressDialog

    override fun getFragmentFromTag(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            Constant.FragmentTags.MALFUNCTION_CATEGORY -> MalfunctionCategoryFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_DESCRIPTION -> MalfunctionDescriptionFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_PHOTOS -> MalfunctionPhotosFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_LOCATION -> MalfunctionLocationFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown fragmentTag: $fragmentTag")
        }
    }

    override fun getContentView() = R.layout.activity_client_new_malfunction
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mActivityPresenter.initPresenter()

        pushFragment(Constant.FragmentTags.MALFUNCTION_CATEGORY)
        progressDialog = ProgressDialog(this)
    }

    override fun onActivityDestroy() {
        progressDialog.dismiss()
        mActivityPresenter.destroyPresenter()

        mRefWatcher.watch(this)
    }

    override fun replaceWithFragment(fragmentTag: String) {
        pushFragment(fragmentTag)
    }

    override fun requestPermission(permission: String, requestCode: Int) {
        mPermissionManager.askForPermission(this, permission, requestCode) { granted ->
            if (granted) {
                val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)

                if (currentFragment is MalfunctionPhotosFragmentCallbacks) {
                    currentFragment.onPermissionGranted()
                }

            } else {
                showErrorMessageDialog("Не удалось получить разрешение на открытие галлереи фото")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun retrieveCategory(category: DamageClaimCategory) {
        malfunctionRequestInfo.damageClaimCategory = category
    }

    override fun retrieveDescription(description: String) {
        malfunctionRequestInfo.damageClaimDescription = description
    }

    override fun retrievePhotos(photos: List<String>) {
        malfunctionRequestInfo.damageClaimPhotos = ArrayList(photos)
    }

    override fun retrieveLocation(location: LatLng) {
        malfunctionRequestInfo.damageClaimLocation = location
    }

    override fun onSendPhotosButtonClick() {
        mActivityPresenter.sendMalfunctionRequestToServer(malfunctionRequestInfo)
    }

    override fun onViewReady() {

    }

    override fun onViewStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerChooseCategoryActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewMalfunctionActivityModule(ClientNewMalfunctionActivityModule(this))
                .build()
                .inject(this)
    }

    override fun onShowToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    override fun onInitProgressDialog(filesCount: Int) {
        progressDialog.init(filesCount)
        progressDialog.show()
    }

    override fun onProgressDialogUpdate(progress: Int) {
        progressDialog.setProgress(progress)
    }

    override fun onFileUploaded() {
        progressDialog.onFileUploaded()
    }

    override fun onAllFilesUploaded() {
        progressDialog.hide()
    }

    override fun onResetProgressDialog() {
        progressDialog.reset()
    }

    override fun onFileUploadingError(e: Throwable) {
        progressDialog.hide()

        runOnUiThread {
            if (e.message != null) {
                showErrorMessageDialog(e.message!!, true)
            }  else {
                showErrorMessageDialog("Неизвестная ошибка при попытке заливки фото", true)
            }
        }
    }

    override fun onMalfunctionRequestSuccessfullyCreated() {
        showToast("Заявка успешно создана", Toast.LENGTH_LONG)
        //runActivity(ClientMainActivity::class.java, true)
    }

    override fun onFileSizeExceeded() {
        showToast("Размер одного из выбранных изображений превышает лимит", Toast.LENGTH_LONG)
    }

    override fun onRequestSizeExceeded() {
        showToast("Размер двух и более изображений превышает лимит", Toast.LENGTH_LONG)
    }

    override fun onAllFileServersAreNotWorking() {
        showToast("Не удалось обработать запрос. Сервера не работают. Попробуйте повторить запрос позже.", Toast.LENGTH_LONG)
    }

    override fun onServerDatabaseError() {
        showToast("Ошибка БД на сервере. Попробуйте повторить запрос позже.", Toast.LENGTH_LONG)
    }

    override fun onCouldNotConnectToServer(error: Throwable) {
        showToast("Не удалось подключиться к серверу", Toast.LENGTH_LONG)
    }

    override fun onPhotosAreNotSet() {
        showToast("Не выбраны фото поломки", Toast.LENGTH_LONG)
    }

    override fun onSelectedPhotoDoesNotExists() {
        showToast("Не удалось прочитать фото с диска (оно было удалено или перемещено)", Toast.LENGTH_LONG)
    }

    override fun onResponseBodyIsEmpty() {
        showErrorMessageDialog("Response body is empty!", true)
    }

    override fun onFileAlreadySelected() {
        showToast("Нельзя отправить два одинаковых файла", Toast.LENGTH_LONG)
    }

    override fun onUnknownError(error: Throwable) {
        if (error.message != null) {
            showErrorMessageDialog(error.message!!)
        } else {
            showErrorMessageDialog("Неизвестная ошибка")
        }
    }

    override fun onBackPressed() {
        val fragmentsCount = supportFragmentManager.backStackEntryCount
        if (fragmentsCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
