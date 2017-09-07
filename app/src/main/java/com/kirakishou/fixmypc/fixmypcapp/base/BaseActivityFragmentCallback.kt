package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 9/3/2017.
 */
interface BaseActivityFragmentCallback {
    fun onShowToast(message: String)
    fun onUnknownError(error: Throwable)
}