package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.google.gson.annotations.SerializedName
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode

/**
 * Created by kirakishou on 7/25/2017.
 */
class LoginResponse(@SerializedName(Constant.SerializedNames.SESSION_ID_SERIALIZED_NAME) val sessionId: String,
                    @SerializedName(Constant.SerializedNames.ACCOUNT_TYPE_SERIALIZED_NAME) val accountType: AccountType,
                    serverError: ServerErrorCode) : StatusResponse(serverError)