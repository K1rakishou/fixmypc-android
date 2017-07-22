package com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params

import com.google.gson.annotations.SerializedName

/**
 * Created by kirakishou on 7/22/2017.
 */
data class TestRequestParams(@SerializedName("login") val login: String,
                             @SerializedName("password") val password: String)