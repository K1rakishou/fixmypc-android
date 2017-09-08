package com.kirakishou.fixmypc.fixmypcapp.ui.fragment.malfunction


import android.Manifest
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import com.jakewharton.rxbinding2.view.RxView
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragment
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamagePhoto
import com.kirakishou.fixmypc.fixmypcapp.ui.activity.ClientNewMalfunctionActivityFragmentCallback
import com.kirakishou.fixmypc.fixmypcapp.ui.adapter.DamageClaimPhotosAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File


class DamageClaimPhotosFragment : BaseFragment<Nothing>(),
        DamageClaimPhotosFragmentCallbacks,
        DamageClaimPhotosAdapter.PhotoClickCallback {

    @BindView(R.id.photo_recycler_view)
    lateinit var mPhotoRecyclerView: RecyclerView

    @BindView(R.id.button_send_application)
    lateinit var mButtonSendApplication: AppCompatButton

    lateinit var mPhotoAdapter: DamageClaimPhotosAdapter

    override fun getContentView() = R.layout.fragment_damage_claim_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        initBindings()
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

    private fun initBindings() {
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
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.onSendPhotosButtonClick()
    }

    private fun setMalfunctionPhotos(photos: ArrayList<String>) {
        val activityHolder = activity as ClientNewMalfunctionActivityFragmentCallback
        activityHolder.retrievePhotos(photos)
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
