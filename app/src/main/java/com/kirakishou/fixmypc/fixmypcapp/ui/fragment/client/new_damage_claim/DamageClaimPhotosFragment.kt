package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.client.new_damage_claim


import android.Manifest
import android.animation.AnimatorSet
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
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
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamagePhotoDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewDamageClaimActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewDamageClaimActivity
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimAddPhotosAdapter
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.PermissionGrantedCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.interfaces.RequestPermissionCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.navigator.ClientNewDamageClaimActivityNavigator
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class DamageClaimPhotosFragment : BaseFragment<ClientNewDamageClaimActivityViewModel>(),
        PermissionGrantedCallback,
        DamageClaimAddPhotosAdapter.PhotoClickCallback {

    @BindView(R.id.photo_recycler_view)
    lateinit var mPhotoRecyclerView: RecyclerView

    @BindView(R.id.button_load_next)
    lateinit var mButtonLoadNext: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    @Inject
    lateinit var mNavigator: ClientNewDamageClaimActivityNavigator

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mRefWatcher: RefWatcher

    lateinit var mAdapter: DamageClaimAddPhotosAdapter

    override fun initViewModel(): ClientNewDamageClaimActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewDamageClaimActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentViewCreated(savedInstanceState: Bundle?) {
        initRx()
        initRecyclerView()
    }

    override fun onFragmentViewDestroy() {
        mRefWatcher.watch(this)
    }

    private fun initRecyclerView() {
        mAdapter = DamageClaimAddPhotosAdapter(activity, this, mImageLoader)
        mAdapter.init()

        //we need a button so we can add photos
        mAdapter.add(AdapterItem(AdapterItemType.VIEW_ADD_BUTTON))

        val layoutManager = GridLayoutManager(activity,
                AndroidUtils.calculateColumnsCount(activity, Constant.Views.PHOTO_ADAPTER_VIEW_WIDTH))

        mPhotoRecyclerView.layoutManager = layoutManager
        mPhotoRecyclerView.setHasFixedSize(true)
        mPhotoRecyclerView.adapter = mAdapter
        mPhotoRecyclerView.isNestedScrollingEnabled = false
    }

    private fun initRx() {
        mCompositeDisposable += RxView.clicks(mButtonLoadNext)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    setMalfunctionPhotos(mAdapter.getPhotos())
                    loadNextFragment()
                }, { error ->
                    Timber.e(error)
                })
    }

    private fun loadNextFragment() {
        mNavigator.navigateToDamageClaimSendRequestFragment()
    }

    private fun setMalfunctionPhotos(photos: ArrayList<String>) {
        getViewModel().setPhotos(photos)
    }

    override fun onPhotoAddClick(position: Int) {
        val activityHolder = activity as RequestPermissionCallback
        activityHolder.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constant.PermissionCodes.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun onPhotoRemoveClick(position: Int) {
        mAdapter.remove(position)

        if (mAdapter.getPhotosCount() <= 0) {
            mButtonLoadNext.isEnabled = false
        }
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
            EasyImage.handleActivityResult(requestCode, resultCode, data, this@DamageClaimPhotosFragment.activity, object : DefaultCallback() {
                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource?, type: Int) {
                    Timber.e(e)
                }

                override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    onImages(imageFiles)
                }
            })
        }
    }

    private fun onImages(imageFiles: List<File>) {
        for (file in imageFiles) {
            mAdapter.add(AdapterItem(DamagePhotoDTO(file.absolutePath), AdapterItemType.VIEW_PHOTO))
        }

        mAdapter.runOnAdapterHandler {
            if (mAdapter.getPhotosCount() > 0) {
                mButtonLoadNext.isEnabled = true
            }
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
        DaggerClientNewDamageClaimActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule(activity as ClientNewDamageClaimActivity))
                .build()
                .inject(this)
    }
}
