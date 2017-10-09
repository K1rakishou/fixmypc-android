package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 10/8/2017.
 */
class UpdateSpecialistProfileResponse(errorCode: ErrorCode.Remote,

                                      @Expose
                                      @SerializedName("nsp_photo_name")
                                      val newPhotoName: String = "") : StatusResponse(errorCode)