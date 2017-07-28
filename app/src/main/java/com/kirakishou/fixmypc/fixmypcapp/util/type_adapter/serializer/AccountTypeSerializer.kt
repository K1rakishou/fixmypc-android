package com.kirakishou.fixmypc.fixmypcapp.util.type_adapter.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import java.lang.reflect.Type

/**
 * Created by kirakishou on 7/27/2017.
 */
class AccountTypeSerializer : JsonSerializer<AccountType> {

    override fun serialize(src: AccountType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val result = JsonObject()
        result.addProperty("account_type", src!!.value)

        return result
    }
}