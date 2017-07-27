package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 7/25/2017.
 */
data class LoginResponse(@SerializedName("session_id") val sessionId: String,
                         @SerializedName("account_type") val accountType: Int) : StatusResponse(0)