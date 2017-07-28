package com.kirakishou.fixmypc.fixmypcapp.util.type_adapter.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode
import java.lang.reflect.Type

/**
 * Created by kirakishou on 7/27/2017.
 */
class StatusCodeSerializer : JsonSerializer<StatusCode> {

    override fun serialize(src: StatusCode?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val result = JsonObject()
        result.addProperty("status_code", src!!.ordinal)

        return result
    }
}