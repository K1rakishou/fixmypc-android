package com.kirakishou.fixmypc.fixmypcapp.helper.util.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode

/**
 * Created by kirakishou on 7/27/2017.
 */
class ErrorCodeRemoteTypeAdapter : TypeAdapter<ErrorCode.Remote>() {

    override fun read(input: JsonReader): ErrorCode.Remote {
        val secValue = input.nextInt()
        return ErrorCode.Remote.from(secValue)
    }

    override fun write(output: JsonWriter, secValue: ErrorCode.Remote) {
        output.jsonValue(Constant.SerializedNames.SERVER_ERROR_CODE_SERIALIZED_NAME)!!.value(secValue.value)
    }
}