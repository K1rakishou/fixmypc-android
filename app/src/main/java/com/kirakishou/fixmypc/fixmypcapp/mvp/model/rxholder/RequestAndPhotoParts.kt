package com.kirakishou.fixmypc.fixmypcapp.mvp.model.rxholder

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.MalfunctionRequest
import okhttp3.MultipartBody

/**
 * Created by kirakishou on 8/5/2017.
 */
class RequestAndPhotoParts(val request: MalfunctionRequest,
                           val photoParts: ArrayList<MultipartBody.Part>)