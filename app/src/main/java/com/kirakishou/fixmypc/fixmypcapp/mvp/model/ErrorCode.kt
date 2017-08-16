package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class ErrorCode {

    enum class Remote(val value: Int) {
        //Remote Error Code
        REC_OK(0),

        //login errors
        REC_WRONG_LOGIN_OR_PASSWORD(1),
        REC_LOGIN_ALREADY_EXISTS(2),
        REC_LOGIN_IS_INCORRECT(3),
        REC_PASSWORD_IS_INCORRECT(4),
        REC_ACCOUNT_TYPE_IS_INCORRECT(5),

        //malfunction request errors
        REC_NO_FILES_WERE_SELECTED_TO_UPLOAD(6),
        REC_IMAGES_COUNT_EXCEEDED(7),
        REC_FILE_SIZE_EXCEEDED(8),
        REC_REQUEST_SIZE_EXCEEDED(9),
        REC_ALL_FILE_SERVERS_ARE_NOT_WORKING(10),

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
        LEC_MAI_CATEGORY_IS_NOT_SET(3),

        LEC_FILE_SIZE_EXCEEDED(4),
        LEC_SELECTED_PHOTO_DOES_NOT_EXIST(5);

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