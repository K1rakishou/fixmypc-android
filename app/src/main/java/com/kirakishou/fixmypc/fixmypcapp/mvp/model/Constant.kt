package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/21/2017.
 */
object Constant {

    object SerializedNames {
        const val LOGIN_SERIALIZED_NAME = "login"
        const val PASSWORD_SERIALIZED_NAME = "password"
        const val ACCOUNT_TYPE_SERIALIZED_NAME = "account_type"
        const val SESSION_ID_SERIALIZED_NAME = "session_id"
        const val SERVER_ERROR_CODE_SERIALIZED_NAME = "server_error_code"
    }

    val APPLICATION_ID = "com.kirakishou.fixmypc.fixmypcapp"
    val SHARED_PREFS_PREFIX = "${APPLICATION_ID}_SHARED_PREF"
}