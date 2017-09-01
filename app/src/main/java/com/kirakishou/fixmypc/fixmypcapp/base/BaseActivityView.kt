package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 7/20/2017.
 */
interface BaseActivityView {
    fun onShowToast(message: String)
    fun onUnknownError(error: Throwable)
}