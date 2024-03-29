package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input

/**
 * Created by kirakishou on 10/2/2017.
 */
interface RespondedSpecialistsActivityInputs {
    fun getRespondedSpecialistsSubject(damageClaimId: Long, skip: Long, count: Long)
    fun assignSpecialist(userId: Long, damageClaimId: Long)
    fun markResponseViewed(damageClaimId: Long, userId: Long)
}