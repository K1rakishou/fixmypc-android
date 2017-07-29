package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
enum class ServerErrorCode(val value: Int) {
    SEC_OK(0),
    SEC_WRONG_LOGIN_OR_PASSWORD(1),
    SEC_LOGIN_ALREADY_EXISTS(2),
    SEC_LOGIN_IS_INCORRECT(3),
    SEC_PASSWORD_IS_INCORRECT(4),
    SEC_ACCOUNT_TYPE_IS_INCORRECT(5),
    SEC_UNKNOWN_SERVER_ERROR(-1);

    companion object {
        fun from(value: Int): ServerErrorCode {
            for (code in ServerErrorCode.values()) {
                if (code.value == value) {
                    return code
                }
            }

            throw IllegalArgumentException("Unknown value: $value")
        }
    }
}