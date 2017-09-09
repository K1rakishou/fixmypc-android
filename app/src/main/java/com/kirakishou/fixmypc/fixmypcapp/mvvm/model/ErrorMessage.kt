package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

import android.content.Context
import com.kirakishou.fixmypc.fixmypcapp.R

/**
 * Created by kirakishou on 7/27/2017.
 */
object ErrorMessage {

    fun getRemoteErrorMessage(context: Context, errorCode: ErrorCode.Remote): String {
        val resources = context.resources

        when (errorCode) {
            ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR -> return resources.getString(R.string.rec_unknown_server_error)
            ErrorCode.Remote.REC_OK -> throw IllegalArgumentException("Should not happen since STATUS_OK is not handled by any error handlers")
            ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD -> return resources.getString(R.string.rec_wrong_login_or_password)
            ErrorCode.Remote.REC_LOGIN_ALREADY_EXISTS -> return resources.getString(R.string.rec_login_already_registered)
            ErrorCode.Remote.REC_LOGIN_IS_INCORRECT -> return resources.getString(R.string.rec_login_is_incorrect)
            ErrorCode.Remote.REC_PASSWORD_IS_INCORRECT -> {
                return String.format(resources.getString(R.string.rec_wrong_login_or_password),
                        resources.getInteger(R.integer.min_password_length),
                        resources.getInteger(R.integer.max_password_length))
            }
            ErrorCode.Remote.REC_ACCOUNT_TYPE_IS_INCORRECT -> return resources.getString(R.string.rec_account_type_is_incorrect)
            else -> throw IllegalArgumentException("Unknown statusCode: $errorCode")
        }
    }

    fun getLocalErrorMessage(context: Context, errorCode: ErrorCode.Local): String {
        val resources = context.resources

        when (errorCode) {
            ErrorCode.Local.LEC_MAI_DESCRIPTION_IS_NOT_SET -> return resources.getString(R.string.lec_mai_description_is_not_set)
            ErrorCode.Local.LEC_MAI_CATEGORY_IS_NOT_SET -> return resources.getString(R.string.lec_mai_category_is_not_set)
            ErrorCode.Local.LEC_MAI_PHOTOS_ARE_NOT_SET -> return resources.getString(R.string.lec_mai_photos_are_not_set)

            else -> throw IllegalArgumentException("Unknown statusCode: $errorCode")
        }
    }
}