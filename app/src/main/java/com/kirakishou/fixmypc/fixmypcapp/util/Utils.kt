package com.kirakishou.fixmypc.fixmypcapp.util

import android.os.Looper
import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
object Utils {
    fun checkIsOnMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    fun throwIfOnMainThread() {
        if (checkIsOnMainThread()) {
            Timber.e("Current operation cannot be executed on main thread")
            throw IllegalStateException("Current operation cannot be executed on main thread")
        }
    }
}