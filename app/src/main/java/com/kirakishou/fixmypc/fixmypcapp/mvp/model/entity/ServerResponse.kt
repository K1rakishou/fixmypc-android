package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode

/**
 * Created by kirakishou on 7/27/2017.
 */

sealed class ServerResponse<out R> {
    data class Success<out T>(val value: T): ServerResponse<T>()
    data class ServerError<out T>(val serverErrorCode: ServerErrorCode): ServerResponse<T>()
    data class UnknownError<out T>(val error: Throwable): ServerResponse<T>()
}