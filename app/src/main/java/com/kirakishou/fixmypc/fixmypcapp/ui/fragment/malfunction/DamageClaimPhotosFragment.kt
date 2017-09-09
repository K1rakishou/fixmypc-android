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
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.FixmypcApplication
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.di.component.DaggerChooseCategoryActivityComponent
import com.kirakishou.fixmypc.fixmypcapp.di.module.ClientNewDamageClaimActivityModule
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamagePhoto
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.ClientNewMalfunctionActivityViewModel
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.factory.ClientNewMalfunctionActivityViewModelFactory
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimPhotosAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class DamageClaimPhotosFragment : BaseFragment<ClientNewMalfunctionActivityViewModel>(),
        DamageClaimPhotosFragmentCallbacks,
        DamageClaimPhotosAdapter.PhotoClickCallback {

    @BindView(R.id.photo_recycler_view)
    lateinit var mPhotoRecyclerView: RecyclerView

    @BindView(R.id.button_send_application)
    lateinit var mButtonSendApplication: AppCompatButton

    @Inject
    lateinit var mViewModelFactory: ClientNewMalfunctionActivityViewModelFactory

    lateinit var mPhotoAdapter: DamageClaimPhotosAdapter

    override fun getViewModel0(): ClientNewMalfunctionActivityViewModel? {
        return ViewModelProviders.of(activity, mViewModelFactory).get(ClientNewMalfunctionActivityViewModel::class.java)
    }

    override fun getContentView() = R.layout.fragment_damage_claim_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initRx()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mPhotoAdapter = DamageClaimPhotosAdapter(activity, this)

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
    }

    private fun sendApplicationToServer() {
        getViewModel().mInputs.sendMalfunctionRequestToServer()
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
                        mPhotoAdapter.add(AdapterItem(DamagePhoto(file.absolutePath), AdapterItemType.VIEW_PHOTO))
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

    override fun onFragmentStop() {

    }

    override fun resolveDaggerDependency() {
        DaggerChooseCategoryActivityComponent.builder()
                .applicationComponent(FixmypcApplication.applicationComponent)
                .clientNewDamageClaimActivityModule(ClientNewDamageClaimActivityModule())
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
