package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/21/2017.
 */
object Constant {
    private val APPLICATION_ID = "com.kirakishou.fixmypc.fixmypcapp"
    val SHARED_PREFS_PREFIX = "${APPLICATION_ID}_SHARED_PREF"
    val DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS = 4
    val RECYCLER_VIEW_MAX_COLUMNS_COUNT = 10
    val MAX_DAMAGE_CLAIMS_PER_PAGE = 5
    val MAX_FILE_SIZE = 5242880

    object SerializedNames {
        const val LOGIN = "login"
        const val PASSWORD = "password"
        const val ACCOUNT_TYPE = "account_type"
        const val SESSION_ID = "session_id"
        const val SERVER_ERROR_CODE = "server_error_code"
        const val DAMAGE_CATEGORY = "damage_category"
        const val DAMAGE_DESCRIPTION = "damage_description"
        const val DAMAGE_LOCATION = "damage_location"
        const val LOCATION_LAT = "lat"
        const val LOCATION_LON = "lon"
    }

    object FragmentTags {
        private val BASE_FRAGMENT_TAG = "${APPLICATION_ID}_FRAGMENT_TAG"
        val DAMAGE_CATEGORY = "${BASE_FRAGMENT_TAG}_DAMAGE_CATEGORY"
        val DAMAGE_DESCRIPTION = "${BASE_FRAGMENT_TAG}_DAMAGE_DESCRIPTION"
        val DAMAGE_PHOTOS = "${BASE_FRAGMENT_TAG}_DAMAGE_PHOTOS"
        val DAMAGE_LOCATION = "${BASE_FRAGMENT_TAG}_DAMAGE_LOCATION"

        val ACTIVE_DAMAGE_CLAIMS_LIST = "${BASE_FRAGMENT_TAG}_ACTIVE_DAMAGE_CLAIMS_LIST"
    }

    object ImageSize {
        val LARGE = "large"
        val MEDIUM = "medium"
        val SMALL = "small"
    }

    object Views {
        val PHOTO_ADAPTER_VIEW_WITH: Int = 128
        val DAMAGE_CLAIM_ADAPTER_VIEW_WITH = 288
    }

    object PermissionCodes {
        val PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 0x1
    }

    object Room {
        object TableName {
            const val DAMAGE_CLAIM_ENTITY_TABLE_NAME = "damage_claims"
            const val DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME = "damage_claims_photos"
        }

        object ColumnName {

        }
    }
}