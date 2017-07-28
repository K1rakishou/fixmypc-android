package com.kirakishou.fixmypc.fixmypcapp.util.adapter_factory

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode

/**
 * Created by kirakishou on 7/27/2017.
 */
class StatusCodeTypeAdapter<T> : TypeAdapter<T>() {

    override fun read(input: JsonReader?): T {
        val statusCodeValue = input!!.nextInt()
        return StatusCode.from(statusCodeValue) as T
    }

    override fun write(output: JsonWriter?, value: T) {
        val statusCodeValue = value as StatusCode

        output!!.jsonValue("status_code")!!.value(statusCodeValue.value)
    }
}