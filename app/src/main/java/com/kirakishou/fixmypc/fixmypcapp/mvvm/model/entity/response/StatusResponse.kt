package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 7/25/2017.
 */
open class StatusResponse(@Expose
                          @SerializedName(Constant.SerializedNames.SERVER_ERROR_CODE) var errorCode: ErrorCode.Remote)