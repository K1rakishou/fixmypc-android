package com.kirakishou.fixmypc.fixmypcapp.util.converter

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import retrofit2.HttpException

/**
 * Created by kirakishou on 7/28/2017.
 */
open class ErrorBodyConverterImpl(val gson: Gson): ErrorBodyConverter {

    override fun <T> convert(error: HttpException, clazz: Class<*>): Fickle<T> {
        val responseBody = error.response().errorBody() ?: return Fickle.empty()
        return Fickle.of(gson.fromJson<T>(responseBody.string(), clazz))
    }
}