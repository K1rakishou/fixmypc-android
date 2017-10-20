package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input

/**
 * Created by kirakishou on 9/29/2017.
 */
interface ClientMainActivityInputs {
    fun getActiveClientDamageClaimSubject(skip: Long, count: Long)
    fun getInactiveClientDamageClaimSubject(skip: Long, count: Long)
    fun getClientProfile()
}