package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private val url = "$mBaseUrl/v1/api/image/"

    fun loadDamageClaimImageFromNetInto(userId: Long, imageName: String, view: ImageView) {
        if (mWifiUtils.isWifiConnected()) {
            Glide.with(mContext)
                    .load("$url/$userId/${Constant.ImageSize.SMALL}/$imageName/")
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_no_wifi)
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        }
    }

    fun loadProfileImageFromNetInto() {

    }

    fun loadImageFromDiskInto(file: File, imageView: ImageView) {
        Glide.with(mContext)
                .load(file)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
    }
}