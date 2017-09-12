package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.connectivityManager
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import java.io.File

/**
 * Created by kirakishou on 9/11/2017.
 */
class ImageLoader(protected val mContext: Context,
                  mBaseUrl: String) {
    private val url = "$mBaseUrl/v1/api/image/"

    fun loadImageFromNetInto(imageName: String, view: ImageView) {
        if (!mContext.connectivityManager.isActiveNetworkMetered) {
            Glide.with(mContext)
                    .load("$url/$imageName/${Constant.ImageSize.SMALL}/")
                    .apply(RequestOptions().centerCrop())
                    .into(view)
        } else {
            TODO("show NO_WIFI_AVAILABLE image")
        }
    }

    fun loadImageFromDiskInto(file: File, imageView: ImageView) {
        Glide.with(mContext)
                .load(file)
                .apply(RequestOptions()
                        .fitCenter()
                        .centerCrop())
                .into(imageView)
    }
}