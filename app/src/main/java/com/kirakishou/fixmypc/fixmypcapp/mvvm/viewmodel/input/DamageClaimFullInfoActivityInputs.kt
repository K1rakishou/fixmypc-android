package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input

/**
 * Created by kirakishou on 10/15/2017.
 */
interface DamageClaimFullInfoActivityInputs {
    fun checkHasAlreadyRespondedToDamageClaim(damageClaimId: Long)
    fun respondToDamageClaim(damageClaimId: Long)
}