package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/21/2017.
 */
object Constant {
    private val APPLICATION_ID = "com.kirakishou.fixmypc.fixmypcapp"
    val SHARED_PREFS_PREFIX = "${APPLICATION_ID}_SHARED_PREF"
    val MALFUNCTION_PHOTO_ADAPTER_MAX_PHOTOS = 3
    val RECYCLERVIEW_MAX_COLUMNS_COUNT = 10
    val MAX_FILE_SIZE = 5242880

    object SerializedNames {
        const val LOGIN_SERIALIZED_NAME = "login"
        const val PASSWORD_SERIALIZED_NAME = "password"
        const val ACCOUNT_TYPE_SERIALIZED_NAME = "account_type"
        const val SESSION_ID_SERIALIZED_NAME = "session_id"
        const val SERVER_ERROR_CODE_SERIALIZED_NAME = "server_error_code"
        const val MALFUNCTION_CATEGORY = "m_category"
        const val MALFUNCTION_DESCRIPTION = "m_description"
    }

    object FragmentTags {
        private val BASE_FRAGMENT_TAG = "${APPLICATION_ID}_FRAGMENT_TAG"
        val MALFUNCTION_CATEGORY_FRAGMENT_TAG = "${BASE_FRAGMENT_TAG}_CHOOSE_CATEGORY"
        val MALFUNCTION_DESCRIPTION_FRAGMENT_TAG = "${BASE_FRAGMENT_TAG}_MALFUNCTION_DESCRIPTION"
        val MALFUNCTION_PHOTOS_FRAGMENT_TAG = "${BASE_FRAGMENT_TAG}_MALFUNCTION_PHOTOS"
    }

    object Views {
        val PHOTO_ADAPTER_VIEW_WITH: Int = 128
    }

    object PermissionCodes {
        val PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 0x1
    }
}