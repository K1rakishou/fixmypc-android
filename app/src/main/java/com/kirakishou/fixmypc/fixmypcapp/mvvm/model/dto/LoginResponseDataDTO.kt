package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType

/**
 * Created by kirakishou on 9/8/2017.
 */
data class LoginResponseDataDTO(val sessionId: String,
                                val accountType: AccountType)