package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 7/22/2017.
 */
data class LoginRequest(@Expose
                        @SerializedName(Constant.SerializedNames.LOGIN) val login: String,

                        @Expose
                        @SerializedName(Constant.SerializedNames.PASSWORD) val password: String) : BaseRequest