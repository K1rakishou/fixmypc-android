package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.Context

/**
 * Created by kirakishou on 9/11/2017.
 */
class ImageLoader(val mContext: Context) {
    val fileServers = arrayListOf("127.0.0.1:9119", "127.0.0.1:9119", "127.0.0.1:9119", "127.0.0.1:9119")
    val path = "/v1/api/damage_claim_photo/" //{image_type}/{owner_id}/{folder_name}/{image_name:.+}

    private fun parseImageName(imageName: String): ExtractedImageInfo {
        val strings = imageName.split("_")
        val serverIdStr = strings[0].removeRange(0..0)
        val name = strings[1].removeRange(0..0)

        return ExtractedImageInfo(serverIdStr.toInt(), name)
    }

    private fun getFileServerByImageName(imageName: String): String {
        val imageInfo = parseImageName(imageName)

        return fileServers[imageInfo.serverId]
    }

    /*fun formatUrl(imageName: String): String {
        val serverAddress = getFileServerByImageName(imageName)

    }

    fun loadImageInto(imageName: String, view: ImageView) {


        Glide.with(mContext)
                .load()
    }*/

    data class ExtractedImageInfo(val serverId: Int,
                                  val name: String)
}