package com.kirakishou.fixmypc.fixmypcapp.module.activity

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentedActivity
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewMalfunctionActivityModule
import com.kirakishou.fixmypc.fixmypcapp.manager.permission.PermissionManager
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction.MalfunctionCategoryFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction.MalfunctionDescriptionFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction.MalfunctionPhotosFragment
import com.kirakishou.fixmypc.fixmypcapp.module.fragment.malfunction.MalfunctionPhotosFragmentCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.MalfunctionCategory
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity.ClientNewMalfunctionPresenterImpl
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.ClientNewMalfunctionActivityView
import com.kirakishou.fixmypc.fixmypcapp.util.dialog.ProgressDialog
import com.squareup.leakcanary.RefWatcher
import javax.inject.Inject


class ClientNewMalfunctionActivity : BaseFragmentedActivity(), ClientNewMalfunctionActivityView {

    @Inject
    lateinit var mPresenter: ClientNewMalfunctionPresenterImpl

    @Inject
    lateinit var mPermissionManager: PermissionManager

    @Inject
    lateinit var mRefWatcher: RefWatcher

    private val malfunctionRequestInfo = MalfunctionRequestInfo()
    private lateinit var progressDialog: ProgressDialog

    override fun getFragmentFromTag(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            Constant.FragmentTags.MALFUNCTION_CATEGORY_FRAGMENT_TAG -> MalfunctionCategoryFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_DESCRIPTION_FRAGMENT_TAG -> MalfunctionDescriptionFragment.newInstance()
            Constant.FragmentTags.MALFUNCTION_PHOTOS_FRAGMENT_TAG -> MalfunctionPhotosFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown fragmentTag: $fragmentTag")
        }
    }

    override fun getContentView() = R.layout.activity_client_new_malfunction
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onActivityCreate(savedInstanceState: Bundle?, intent: Intent) {
        mPresenter.initPresenter()

        pushFragment(Constant.FragmentTags.MALFUNCTION_CATEGORY_FRAGMENT_TAG)
        progressDialog = ProgressDialog(this)
    }

    override fun onActivityDestroy() {
        mPresenter.destroyPresenter()
        progressDialog.dismiss()

        mRefWatcher.watch(this)
    }

    fun requestPermission(permission: String, requestCode: Int) {
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

    fun setMalfunctionCategory(malfunctionCategory: MalfunctionCategory) {
        this.malfunctionRequestInfo.malfunctionCategory = malfunctionCategory
    }

    fun setMalfunctionDescription(malfunctionDescription: String) {
        this.malfunctionRequestInfo.malfunctionDescription = malfunctionDescription
    }

    fun setMalfunctionPhotos(malfunctionPhotos: ArrayList<String>) {
        this.malfunctionRequestInfo.malfunctionPhotos = malfunctionPhotos
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun sendRequestToServer() {
        mPresenter.sendMalfunctionRequestToServer(malfunctionRequestInfo)
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

    override fun resetProgressDialog() {
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
        runActivity(ClientMainActivity::class.java, true)
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
