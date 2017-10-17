package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/21/2017.
 */
object Constant {
    val APPLICATION_ID = "com.kirakishou.fixmypc.fixmypcapp"
    val SHARED_PREFS_PREFIX = "${APPLICATION_ID}_SHARED_PREF"
    val DAMAGE_CLAIM_PHOTO_ADAPTER_MAX_PHOTOS = 4
    val RECYCLER_VIEW_MAX_COLUMNS_COUNT = 10
    val MAX_DAMAGE_CLAIMS_PER_PAGE = 5L
    val MAX_SPECIALISTS_PROFILES_PER_PAGE = 5L
    val MAX_FILE_SIZE = 5242880
    val MAX_REPO_STORE_ITEMS_TIME = 1000L * 60L * 60L // one hour
    val MAP_ZOOM = 13.5f

    object ReceiverActions {
        val WAIT_FOR_SPECIALIST_PROFILE_UPDATE_NOTIFICATION = "${APPLICATION_ID}_SPECIALIST_PROFILE_UPDATE"
    }

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
        val DAMAGE_SEND_REQUEST = "${BASE_FRAGMENT_TAG}_DAMAGE_SEND_REQUEST"

        val ACTIVE_DAMAGE_CLAIMS_LIST = "${BASE_FRAGMENT_TAG}_ACTIVE_DAMAGE_CLAIMS_LIST"
        val DAMAGE_CLAIM_FULL_INFO = "${BASE_FRAGMENT_TAG}_DAMAGE_CLAIM_FULL_INFO"
        val LOADING_INDICATOR = "${BASE_FRAGMENT_TAG}_LOADING_INDICATOR"
        val CLIENT_MY_DAMAGE_CLAIMS = "${BASE_FRAGMENT_TAG}_CLIENT_MY_DAMAGE_CLAIMS"
        val RESPONDED_SPECIALISTS_LIST = "${BASE_FRAGMENT_TAG}_RESPONDED_SPECIALISTS_LIST"
        val SPECIALIST_FULL_PROFILE = "${BASE_FRAGMENT_TAG}_SPECIALIST_FULL_PROFILE"
        val LOGIN_FRAGMENT = "${BASE_FRAGMENT_TAG}_LOGIN_FRAGMENT"
        val SPECIALIST_PROFILE = "${BASE_FRAGMENT_TAG}_SPECIALIST_PROFILE"
        val UPDATE_SPECIALIST_PROFILE = "${BASE_FRAGMENT_TAG}_UPDATE_SPECIALIST_PROFILE"
        val SPECIALIST_OPTIONS = "${BASE_FRAGMENT_TAG}_SPECIALIST_OPTIONS"
        val CLIENT_OPTIONS = "${BASE_FRAGMENT_TAG}_CLIENT_OPTIONS"
        val CLIENT_PROFILE = "${BASE_FRAGMENT_TAG}_CLIENT_PROFILE"
    }

    object ImageSize {
        val LARGE = "large"
        val MEDIUM = "medium"
        val SMALL = "small"
    }

    object Views {
        val PHOTO_ADAPTER_VIEW_WIDTH: Int = 128
        val DAMAGE_CLAIM_ADAPTER_VIEW_WIDTH = 288
        val SPECIALIST_PROFILE_ADAPTER_VIEW_WIDTH = 384
    }

    object PermissionCodes {
        val PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 0x1
    }

    object Room {
        object TableName {
            const val DAMAGE_CLAIM_ENTITY_TABLE_NAME = "damage_claims"
            const val DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME = "damage_claims_photos"
        }
    }
}