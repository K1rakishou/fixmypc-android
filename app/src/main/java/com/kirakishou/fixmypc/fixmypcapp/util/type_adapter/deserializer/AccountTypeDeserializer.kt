package com.kirakishou.fixmypc.fixmypcapp.util.type_adapter.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import java.lang.reflect.Type

/**
 * Created by kirakishou on 7/27/2017.
 */
class AccountTypeDeserializer : JsonDeserializer<AccountType> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): AccountType {
        return AccountType.Client
    }
}