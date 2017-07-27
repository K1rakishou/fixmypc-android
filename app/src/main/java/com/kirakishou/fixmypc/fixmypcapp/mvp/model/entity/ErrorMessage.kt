package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import android.content.Context
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode

/**
 * Created by kirakishou on 7/27/2017.
 */
object ErrorMessage {

    fun getErrorMessage(context: Context, statusCode: StatusCode): String {
        val resources = context.resources

        when (statusCode) {
            StatusCode.STATUS_UNKNOWN_SERVER_ERROR -> return resources.getString(R.string.unknown_server_error)
            StatusCode.STATUS_OK -> throw IllegalArgumentException("Should not happen since STATUS_OK is not handled by any error handlers")
            StatusCode.STATUS_WRONG_LOGIN_OR_PASSWORD -> return resources.getString(R.string.wrong_login_or_password)
            StatusCode.STATUS_LOGIN_ALREADY_EXISTS -> return resources.getString(R.string.login_already_registered)
            StatusCode.STATUS_LOGIN_IS_INCORRECT -> return resources.getString(R.string.login_is_incorrect)
            StatusCode.STATUS_PASSWORD_IS_INCORRECT -> {
                return String.format(resources.getString(R.string.wrong_login_or_password),
                        resources.getInteger(R.integer.min_password_length),
                        resources.getInteger(R.integer.max_password_length))
            }
            StatusCode.STATUS_ACCOUNT_TYPE_IS_INCORRECT -> return resources.getString(R.string.account_type_is_incorrect)
            else -> throw IllegalArgumentException("Unknown statusCode: $statusCode")
        }
    }
}