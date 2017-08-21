package com.kirakishou.fixmypc.fixmypcapp.util.converter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import retrofit2.HttpException

/**
 * Created by kirakishou on 7/28/2017.
 */
interface ErrorBodyConverter {
    fun <T> convert(error: HttpException, clazz: Class<*>): Fickle<T>
}