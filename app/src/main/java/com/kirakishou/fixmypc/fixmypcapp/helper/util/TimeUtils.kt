package com.kirakishou.fixmypc.fixmypcapp.helper.util

/**
 * Created by kirakishou on 9/12/2017.
 */
object TimeUtils {

    @Synchronized
    fun getTimeFast(): Long = System.currentTimeMillis()
}