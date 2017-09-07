package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 7/26/2017.
 */
data class SignupRequest(@Expose
                         @SerializedName(Constant.SerializedNames.LOGIN) val login: String,

                         @Expose
                         @SerializedName(Constant.SerializedNames.PASSWORD) val password: String,

                         @Expose
                         @SerializedName(Constant.SerializedNames.ACCOUNT_TYPE) val accountType: AccountType) : BaseRequest