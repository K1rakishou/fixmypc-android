package com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType

/**
 * Created by kirakishou on 9/8/2017.
 */
data class LoginResponseDataDTO(val sessionId: String,
                                val accountType: AccountType)