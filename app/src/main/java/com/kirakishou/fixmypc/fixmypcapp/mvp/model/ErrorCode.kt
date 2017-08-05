package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class ErrorCode {

    enum class Remote(val value: Int) {
        //Remote Error Code
        REC_OK(0),
        REC_WRONG_LOGIN_OR_PASSWORD(1),
        REC_LOGIN_ALREADY_EXISTS(2),
        REC_LOGIN_IS_INCORRECT(3),
        REC_PASSWORD_IS_INCORRECT(4),
        REC_ACCOUNT_TYPE_IS_INCORRECT(5),
        REC_UNKNOWN_SERVER_ERROR(-1);

        companion object {
            fun from(value: Int): ErrorCode.Remote {
                for (code in ErrorCode.Remote.values()) {
                    if (code.value == value) {
                        return code
                    }
                }

                throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }

    enum class Local(val value: Int) {
        LEC_OK(0),

        //malfunction application info
        LEC_MAI_PHOTOS_ARE_NOT_SET(1),
        LEC_MAI_DESCRIPTION_IS_NOT_SET(2),
        LEC_MAI_CATEGORY_IS_NOT_SET(2);

        companion object {
            fun from(value: Int): ErrorCode.Local {
                for (code in ErrorCode.Local.values()) {
                    if (code.value == value) {
                        return code
                    }
                }

                throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}