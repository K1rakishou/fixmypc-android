package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
enum class StatusCode(val value: Int) {
    STATUS_OK(0),
    STATUS_WRONG_LOGIN_OR_PASSWORD(1),
    STATUS_LOGIN_ALREADY_EXISTS(2),
    STATUS_LOGIN_IS_INCORRECT(3),
    STATUS_PASSWORD_IS_INCORRECT(4),
    STATUS_ACCOUNT_TYPE_IS_INCORRECT(5),
    STATUS_UNKNOWN_SERVER_ERROR(-1);

    companion object {
        fun from(value: Int): StatusCode {
            for (code in StatusCode.values()) {
                if (code.value == value) {
                    return code
                }
            }

            throw IllegalArgumentException("Unknown value: $value")
        }
    }
}