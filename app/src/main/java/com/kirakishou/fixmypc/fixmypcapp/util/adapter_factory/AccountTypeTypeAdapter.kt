package com.kirakishou.fixmypc.fixmypcapp.util.adapter_factory

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType

/**
 * Created by kirakishou on 7/27/2017.
 */

class AccountTypeTypeAdapter<T> : TypeAdapter<T>() {

    override fun read(input: JsonReader?): T {
        val accountTypeValue = input!!.nextInt()
        return AccountType.from(accountTypeValue) as T
    }

    override fun write(output: JsonWriter?, value: T) {
        val accountTypeValue = value as AccountType

        output!!.jsonValue("account_type")!!.value(accountTypeValue.value)
    }
}