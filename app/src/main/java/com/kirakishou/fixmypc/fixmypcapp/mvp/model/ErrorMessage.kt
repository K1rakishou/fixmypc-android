package com.kirakishou.fixmypc.fixmypcapp.mvp.model

import android.content.Context
import com.kirakishou.fixmypc.fixmypcapp.R

/**
 * Created by kirakishou on 7/27/2017.
 */
object ErrorMessage {

    fun getErrorMessage(context: Context, serverErrorCode: ServerErrorCode): String {
        val resources = context.resources

        when (serverErrorCode) {
            ServerErrorCode.SEC_UNKNOWN_SERVER_ERROR -> return resources.getString(R.string.unknown_server_error)
            ServerErrorCode.SEC_OK -> throw IllegalArgumentException("Should not happen since STATUS_OK is not handled by any error handlers")
            ServerErrorCode.SEC_WRONG_LOGIN_OR_PASSWORD -> return resources.getString(R.string.wrong_login_or_password)
            ServerErrorCode.SEC_LOGIN_ALREADY_EXISTS -> return resources.getString(R.string.login_already_registered)
            ServerErrorCode.SEC_LOGIN_IS_INCORRECT -> return resources.getString(R.string.login_is_incorrect)
            ServerErrorCode.SEC_PASSWORD_IS_INCORRECT -> {
                return String.format(resources.getString(R.string.wrong_login_or_password),
                        resources.getInteger(R.integer.min_password_length),
                        resources.getInteger(R.integer.max_password_length))
            }
            ServerErrorCode.SEC_ACCOUNT_TYPE_IS_INCORRECT -> return resources.getString(R.string.account_type_is_incorrect)
            else -> throw IllegalArgumentException("Unknown statusCode: $serverErrorCode")
        }
    }
}