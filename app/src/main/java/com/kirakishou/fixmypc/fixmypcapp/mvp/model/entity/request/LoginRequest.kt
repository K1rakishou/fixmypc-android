package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 7/22/2017.
 */
data class LoginRequest(@SerializedName("login") val login: String,
                        @SerializedName("password") val password: String)