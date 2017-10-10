package com.kirakishou.fixmypc.fixmypcapp.base

import android.os.Bundle

/**
 * Created by kirakishou on 9/3/2017.
 */
interface BaseActivityFragmentCallback {
    fun runActivityWithArgs(clazz: Class<*>, args: Bundle, finishCurrentActivity: Boolean)
    fun runActivity(clazz: Class<*>, finishCurrentActivity: Boolean)
    fun onShowToast(message: String, duration: Int)
    fun onUnknownError(error: Throwable)
}