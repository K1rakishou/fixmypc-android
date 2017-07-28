package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode

/**
 * Created by kirakishou on 7/25/2017.
 */
class LoginResponse(@SerializedName("session_id") val sessionId: String,
                    @SerializedName("account_type") val accountType: Int,
                    status: StatusCode) : StatusResponse(status)