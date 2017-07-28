package com.kirakishou.fixmypc.fixmypcapp.util.converter

import com.google.gson.Gson

/**
 * Created by kirakishou on 7/28/2017.
 */
open class ErrorBodyConverterImpl(val gson: Gson): ErrorBodyConverter {

    override fun <T> convert(errorBody: String, clazz: Class<*>): T {
        return gson.fromJson<T>(errorBody, clazz)
    }
}