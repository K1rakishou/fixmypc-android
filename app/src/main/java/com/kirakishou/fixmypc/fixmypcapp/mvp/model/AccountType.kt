package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
enum class AccountType(val value: Int) {
    Guest(0),
    Client(1),
    Specialist(2);

    companion object {

        fun from(value: Int): AccountType {
            for (type in AccountType.values()) {
                if (type.value == value) {
                    return type
                }
            }

            throw IllegalArgumentException("Unknown value: $value")
        }
    }
}