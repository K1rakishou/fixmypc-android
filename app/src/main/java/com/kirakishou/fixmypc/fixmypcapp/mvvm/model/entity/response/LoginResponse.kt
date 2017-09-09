package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 7/25/2017.
 */
class LoginResponse(@Expose
                    @SerializedName(Constant.SerializedNames.SESSION_ID) val sessionId: String,

                    @Expose
                    @SerializedName(Constant.SerializedNames.ACCOUNT_TYPE) val accountType: AccountType,
                    errorCode: ErrorCode.Remote) : StatusResponse(errorCode)