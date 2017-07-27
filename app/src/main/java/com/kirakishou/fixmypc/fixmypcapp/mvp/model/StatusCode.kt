package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
enum class StatusCode {
    STATUS_UNKNOWN_SERVER_ERROR,
    STATUS_OK,
    STATUS_WRONG_LOGIN_OR_PASSWORD,
    STATUS_LOGIN_ALREADY_EXISTS,
    STATUS_LOGIN_IS_INCORRECT,
    STATUS_PASSWORD_IS_INCORRECT,
    STATUS_ACCOUNT_TYPE_IS_INCORRECT;

    companion object {
        fun from(value: Int): StatusCode {
            for (code in StatusCode.values()) {
                if (code.ordinal == value) {
                    return code
                }
            }

            throw IllegalArgumentException("Unknown value: $value")
        }
    }
}