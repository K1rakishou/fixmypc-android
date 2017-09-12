package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

/**
 * Created by kirakishou on 9/12/2017.
 */
interface AbstractRequest<T> {
    fun execute(): T
}