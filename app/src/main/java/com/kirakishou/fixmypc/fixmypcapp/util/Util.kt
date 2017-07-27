package com.kirakishou.fixmypc.fixmypcapp.util

import android.os.Looper

/**
 * Created by kirakishou on 7/26/2017.
 */
object Util {
    fun checkIsOnMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    fun throwIfOnMainThread() {
        if (checkIsOnMainThread()) {
            throw IllegalStateException("Current operation cannot be executed on main thread")
        }
    }
}