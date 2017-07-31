package com.kirakishou.fixmypc.fixmypcapp.module.fragment


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
import com.kirakishou.fixmypc.fixmypcapp.module.activity.ClientMainActivity
import com.kirakishou.fixmypc.fixmypcapp.module.adapter.MalfunctionPhotosAdapter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItem
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AdapterItemType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionPhoto
import com.kirakishou.fixmypc.fixmypcapp.util.AndroidUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File


class MalfunctionPhotosFragment : BaseFragment(),
        MalfunctionPhotosFragmentCallbacks,
        MalfunctionPhotosAdapter.PhotoClickCallback {

    @BindView(R.id.photo_recycler_view)
    lateinit var mPhotoRecyclerView: RecyclerView

    @BindView(R.id.button_send_application)
    lateinit var mButtonSendApplication: AppCompatButton

    lateinit var mPhotoAdapter: MalfunctionPhotosAdapter

    override fun getContentView() = R.layout.fragment_malfunction_photos
    override fun loadStartAnimations() = AnimatorSet()
    override fun loadExitAnimations() = AnimatorSet()

    override fun onFragmentReady() {
        Timber.e("onFragmentReady")

        initBindings()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mPhotoAdapter = MalfunctionPhotosAdapter(activity, this)

        //we need a button so we can add photos
        mPhotoAdapter.add(AdapterItem(AdapterItemType.Photo.VIEW_ADD_BUTTON))

        val layoutManager = GridLayoutManager(activity,
                AndroidUtils.calculateNoOfColumns(activity, Constant.Views.PHOTO_ADAPTER_VIEW_WITH))

        mPhotoRecyclerView.layoutManager = layoutManager
        mPhotoRecyclerView.setHasFixedSize(true)
        mPhotoRecyclerView.adapter = mPhotoAdapter
    }

    private fun initBindings() {
        addDisposable(RxView.clicks(mButtonSendApplication)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    sendApplicationToServer()
                }, { error ->
                    Timber.e(error)
                }))
    }

    private fun sendApplicationToServer() {
        val activityHolder = activity as ClientMainActivity
        activityHolder.sendApplicationToServer()
    }

    override fun onPhotoAddClick(position: Int) {
        Timber.e("onPhotoAddClick")

        val activityHolder = activity as ClientMainActivity
        activityHolder.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constant.PermissionCodes.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun onPhotoRemoveClick(position: Int) {
        Timber.e("onPhotoRemoveClick")
    }

    override fun onPermissionGranted() {
        EasyImage.openGallery(this, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this@MalfunctionPhotosFragment.activity, object : DefaultCallback() {
                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource?, type: Int) {
                    Timber.e(e)
                }

                override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    for (file in imageFiles) {
                        mPhotoAdapter.add(AdapterItem(MalfunctionPhoto(file.absolutePath), AdapterItemType.Photo.VIEW_PHOTO))
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

    companion object {
        fun newInstance(): Fragment {
            val fragment = MalfunctionPhotosFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
