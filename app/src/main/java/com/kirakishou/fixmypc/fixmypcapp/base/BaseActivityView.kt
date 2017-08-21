package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 7/20/2017.
 */
interface BaseActivityView {
    abstract fun onShowToast(message: String)
    abstract fun onUnknownError(error: Throwable)
}