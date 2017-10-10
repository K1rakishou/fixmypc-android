package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kirakishou.billboards.modules.controller.PhotoView
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import java.io.File

/**
 * Created by kirakishou on 9/11/2017.
 */
class ImageLoader(protected val mContext: Context,
                  protected val mWifiUtils: WifiUtils,
                  mBaseUrl: String) {
    private val damageClaimImagesUrl = "$mBaseUrl/v1/api/image"
    private val specialistProfileImagesUrl = "$mBaseUrl/v1/api/image"
    private val IMAGE_TYPE_DAMAGE_CLAIM = 0
    private val IMAGE_TYPE_PROFILE = 1

    fun loadDamageClaimImageFromNetInto(userId: Long, imageName: String, view: ImageView) {
        if (mWifiUtils.isWifiConnected()) {
            Glide.with(mContext)
                    .load("$damageClaimImagesUrl/$userId/$IMAGE_TYPE_DAMAGE_CLAIM/${Constant.ImageSize.SMALL}/$imageName/")
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_no_wifi)
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        }
    }

    fun loadProfileImageFromNetInto(userId: Long, imageName: String, view: ImageView) {
        if (mWifiUtils.isWifiConnected()) {
            Glide.with(mContext)
                    .load("$specialistProfileImagesUrl/$userId/$IMAGE_TYPE_PROFILE/${Constant.ImageSize.SMALL}/$imageName/")
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_no_wifi)
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        }
    }

    fun loadImageFromNetToPhotoView(photoName: String, userId: Long, photoView: PhotoView) {
        Glide.with(mContext)
                .asBitmap()
                .apply(RequestOptions()
                        .centerCrop())
                .load("$specialistProfileImagesUrl/$userId/$IMAGE_TYPE_PROFILE/${Constant.ImageSize.SMALL}/$photoName/")
                .into(object : SimpleTarget<Bitmap>(photoView.width, photoView.height) {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        photoView.setImageBitmap(resource)
                        photoView.setPhotoAdded()
                    }
                })
    }

    fun loadImageFromDiskInto(file: File, imageView: ImageView) {
        Glide.with(mContext)
                .load(file)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
    }
}