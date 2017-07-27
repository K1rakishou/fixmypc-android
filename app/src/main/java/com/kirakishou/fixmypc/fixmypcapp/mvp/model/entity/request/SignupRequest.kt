package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType

/**
 * Created by kirakishou on 7/26/2017.
 */
data class SignupRequest(@SerializedName("login") val login: String,
                         @SerializedName("password") val password: String,
                         @SerializedName("account_type") val accountType: AccountType)