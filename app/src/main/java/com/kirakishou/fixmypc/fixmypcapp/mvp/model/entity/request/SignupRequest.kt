package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 7/26/2017.
 */
data class SignupRequest(@SerializedName(Constant.SerializedNames.LOGIN_SERIALIZED_NAME) val login: String,
                         @SerializedName(Constant.SerializedNames.PASSWORD_SERIALIZED_NAME) val password: String,
                         @SerializedName(Constant.SerializedNames.ACCOUNT_TYPE_SERIALIZED_NAME) val accountType: AccountType)