package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 8/1/2017.
 */
enum class DamageClaimCategory {
    Computer,
    Notebook,
    Phone;

    companion object {
        fun getString(category: DamageClaimCategory): String {
            return when (category) {
                DamageClaimCategory.Computer -> "Компьютер"
                DamageClaimCategory.Notebook -> "Ноутбук"
                DamageClaimCategory.Phone -> "Телефон"
            }
        }
    }
}