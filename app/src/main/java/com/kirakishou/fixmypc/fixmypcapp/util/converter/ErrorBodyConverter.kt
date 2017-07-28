package com.kirakishou.fixmypc.fixmypcapp.util.converter

/**
 * Created by kirakishou on 7/28/2017.
 */
interface ErrorBodyConverter {
    fun <T> convert(errorBody: String, clazz: Class<*>): T
}