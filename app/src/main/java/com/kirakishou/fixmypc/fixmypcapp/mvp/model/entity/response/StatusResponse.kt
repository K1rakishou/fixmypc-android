package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode

/**
 * Created by kirakishou on 7/25/2017.
 */
open class StatusResponse(@SerializedName("status_code") var status: StatusCode)