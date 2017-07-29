package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode

/**
 * Created by kirakishou on 7/25/2017.
 */
open class StatusResponse(@SerializedName(Constant.SerializedNames.SERVER_ERROR_CODE_SERIALIZED_NAME) var serverError: ServerErrorCode)