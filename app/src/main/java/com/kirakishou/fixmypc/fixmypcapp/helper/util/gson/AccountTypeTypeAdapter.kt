package com.kirakishou.fixmypc.fixmypcapp.helper.util.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant

/**
 * Created by kirakishou on 7/27/2017.
 */

class AccountTypeTypeAdapter : TypeAdapter<AccountType>() {

    override fun read(input: JsonReader): AccountType {
        val accountTypeValue = input.nextInt()
        return AccountType.from(accountTypeValue)
    }

    override fun write(output: JsonWriter, accountTypeValue: AccountType) {
        output.jsonValue(Constant.SerializedNames.ACCOUNT_TYPE_SERIALIZED_NAME)!!.value(accountTypeValue.value)
    }
}