package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode

/**
 * Created by kirakishou on 7/27/2017.
 */

sealed class ServerResponse<out R> {
    data class Success<out T>(val value: T): ServerResponse<T>()
    data class ServerError<out T>(val errorCode: ErrorCode): ServerResponse<T>()
    data class UnknownError<out T>(val error: Throwable): ServerResponse<T>()
}