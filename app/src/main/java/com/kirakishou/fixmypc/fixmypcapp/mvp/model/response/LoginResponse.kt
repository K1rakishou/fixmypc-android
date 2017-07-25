package com.kirakishou.fixmypc.fixmypcapp.mvp.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 7/25/2017.
 */
data class LoginResponse(@SerializedName("session_id") val sessionId: String) : StatusResponse(0)