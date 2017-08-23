package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 7/22/2017.
 */
data class LoginRequest(@SerializedName(Constant.SerializedNames.LOGIN_SERIALIZED_NAME) val login: String,
                        @SerializedName(Constant.SerializedNames.PASSWORD_SERIALIZED_NAME) val password: String) : BaseRequest