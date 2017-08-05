package com.kirakishou.fixmypc.fixmypcapp.util.type_adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode

/**
 * Created by kirakishou on 7/27/2017.
 */
class StatusCodeTypeAdapter<T> : TypeAdapter<T>() {

    override fun read(input: JsonReader?): T {
        val secValue = input!!.nextInt()
        return ErrorCode.Remote.from(secValue) as T
    }

    override fun write(output: JsonWriter?, value: T) {
        val secValue = value as ErrorCode.Remote

        output!!.jsonValue(Constant.SerializedNames.SERVER_ERROR_CODE_SERIALIZED_NAME)!!.value(secValue.value)
    }
}