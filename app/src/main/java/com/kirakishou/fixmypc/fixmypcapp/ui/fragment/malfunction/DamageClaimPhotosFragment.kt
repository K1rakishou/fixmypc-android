package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction


import android.Manifest
import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerClientNewDamageClaimActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.ImageLoader
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamagePhotoDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimPhotosAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class DamageClaimPhotosFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>(),
        DamageClaimPhotosFragmentCallbacks,
        DamageClaimPhotosAdapter.PhotoClickCallback {

    @BindView(R.id.photo_recycler_view)
    lateinit var mPhotoRecyclerView: RecyclerView

    @BindView(R.id.button_send_application)
    lateinit var mButtonSendApplication: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    lateinit var mPhotoAdapter: DamageClaimPhotosAdapter

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        //getViewModel().init()
        initRx()
        initRecyclerView()
    }

    override fun onFragmentViewDestroy() {
    }

    private fun initRecyclerView() {
        mPhotoAdapter = DamageClaimPhotosAdapter(activity, this, mImageLoader)
        mPhotoAdapter.init()

        //we need a button so we can add photos
        mPhotoAdapter.add(AdapterItem(AdapterItemType.VIEW_ADD_BUTTON))

        val layoutManager = GridLayoutManager(activity,
                AndroidUtils.calculateColumnsCount(activity, Constant.Views.PHOTO_ADAPTER_VIEW_WITH))

        mPhotoRecyclerView.layoutManager = layoutManager
        mPhotoRecyclerView.setHasFixedSize(true)
        mPhotoRecyclerView.adapter = mPhotoAdapter
        mPhotoRecyclerView.isNestedScrollingEnabled = false
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mButtonSendApplication)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionPhotos(mPhotoAdapter.getPhotos())
                    sendApplicationToServer()
                }, { error ->
                    Timber.e(error)
                })

        mCompositeDisposable += getViewModel().mOutputs.onMalfunctionRequestSuccessfullyCreated()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onMalfunctionRequestSuccessfullyCreated() })

        mCompositeDisposable += getViewModel().mErrors.onWifiNotConnected()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onWifiNotConnected() })

        mCompositeDisposable += getViewModel().mErrors.onFileSizeExceeded()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onFileSizeExceeded() })

        mCompositeDisposable += getViewModel().mErrors.onAllFileServersAreNotWorking()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAllFileServersAreNotWorking() })

        mCompositeDisposable += getViewModel().mErrors.onServerDatabaseError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onServerDatabaseError() })

        mCompositeDisposable += getViewModel().mErrors.onCouldNotConnectToServer()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onCouldNotConnectToServer() })

        mCompositeDisposable += getViewModel().mErrors.onPhotosAreNotSelected()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onPhotosAreNotSelected() })

        mCompositeDisposable += getViewModel().mErrors.onSelectedPhotoDoesNotExists()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSelectedPhotoDoesNotExists() })

        mCompositeDisposable += getViewModel().mErrors.onResponseBodyIsEmpty()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onResponseBodyIsEmpty() })

        mCompositeDisposable += getViewModel().mErrors.onFileAlreadySelected()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onFileAlreadySelected() })

        mCompositeDisposable += getViewModel().mErrors.onBadOriginalFileNameSubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onBadOriginalFileName() })

        mCompositeDisposable += getViewModel().mErrors.onUnknownError()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onUnknownError(it) })

        mCompositeDisposable += getViewModel().mErrors.onRequestSizeExceeded()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ onRequestSizeExceeded() })
    }

    private fun sendApplicationToServer() {
        val checkWifiStatus = true //TODO get this from shared prefs
        getViewModel().mInputs.sendMalfunctionRequestToServer(checkWifiStatus)
    }

    private fun setMalfunctionPhotos(photos: ArrayList<String>) {
        getViewModel().setPhotos(photos)
    }

    override fun onPhotoAddClick(position: Int) {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constant.PermissionCodes.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun onPhotoRemoveClick(position: Int) {
        mPhotoAdapter.remove(position)

        if (mPhotoAdapter.getPhotosCount() <= 0) {
            mButtonSendApplication.isEnabled = false
        }
    }

    override fun onPermissionGranted() {
        if (!isAdded) {
            return
        }

        EasyImage.openGallery(this, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this@DamageClaimPhotosFragment.activity, object : DefaultCallback() {
                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource?, type: Int) {
                    Timber.e(e)
                }

                override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    for (file in imageFiles) {
                        mPhotoAdapter.add(AdapterItem(DamagePhotoDTO(file.absolutePath), AdapterItemType.VIEW_PHOTO))
                    }

                    if (mPhotoAdapter.getPhotosCount() > 0) {
                        mButtonSendApplication.isEnabled = true
                    }
                }

                override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                    super.onCanceled(source, type)

                    /*if (source == EasyImage.ImageSource.CAMERA) {
                        val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@ClientMainActivity)
                        photoFile?.delete()
                    }*/
                }
            })
        }
    }

    private fun runActivity(activityClass: Class<*>, finishCurrentActivity: Boolean = false) {
        if (activity !is ClientNewMalfunctionActivityFragmentCallback) {
            throw IllegalStateException("Activity should implement BaseActivityFragmentCallback!")
        }

        (activity as ClientNewMalfunctionActivityFragmentCallback).startActivity(activityClass, finishCurrentActivity)
    }

    private fun onMalfunctionRequestSuccessfullyCreated() {
        showToast("Заявка успешно создана", Toast.LENGTH_LONG)
        runActivity(ClientMainActivity::class.java, true)
    }

    private fun onWifiNotConnected() {
        showToast("Отсутствует подключение WiFi. Если Вы хотите хотите отправлять заявки даже при отключенном WiFi - " +
                "отключите в настройках опцию \"Запретить отправлять заявки при отключенном WiFi\"", Toast.LENGTH_LONG)
    }

    private fun onFileSizeExceeded() {
        showToast("Размер одного из выбранных изображений превышает лимит", Toast.LENGTH_LONG)
    }

    private fun onRequestSizeExceeded() {
        showToast("Размер двух и более изображений превышает лимит", Toast.LENGTH_LONG)
    }

    private fun onAllFileServersAreNotWorking() {
        showToast("Не удалось обработать запрос. Сервера не работают. Попробуйте повторить запрос позже.", Toast.LENGTH_LONG)
    }

    private fun onServerDatabaseError() {
        showToast("Ошибка БД на сервере. Попробуйте повторить запрос позже.", Toast.LENGTH_LONG)
    }

    private fun onCouldNotConnectToServer() {
        showToast("Не удалось подключиться к серверу", Toast.LENGTH_LONG)
    }

    private fun onPhotosAreNotSelected() {
        showToast("Не выбраны фото поломки", Toast.LENGTH_LONG)
    }

    private fun onSelectedPhotoDoesNotExists() {
        showToast("Не удалось прочитать фото с диска (оно было удалено или перемещено)", Toast.LENGTH_LONG)
    }

    private fun onResponseBodyIsEmpty() {
        showToast("Response body is empty!", Toast.LENGTH_LONG)
    }

    private fun onFileAlreadySelected() {
        showToast("Нельзя отправить два одинаковых файла", Toast.LENGTH_LONG)
    }

    private fun onBadOriginalFileName() {
        showToast("Попытка отправить файл не являющийся изображением", Toast.LENGTH_LONG)
    }

    private fun onUnknownError(error: Throwable) {
        super.unknownError(error)
    }

    override fun resolveDaggerDependency() {
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = DamageClaimPhotosFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
