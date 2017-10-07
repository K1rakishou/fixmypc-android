package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 9/3/2017.
 */
interface BaseActivityFragmentCallback {
    fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean)
    fun onShowToast(message: String, duration: Int)
    fun onUnknownError(error: Throwable)
}