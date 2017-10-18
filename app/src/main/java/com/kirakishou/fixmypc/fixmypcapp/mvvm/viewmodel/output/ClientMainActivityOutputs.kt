package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsWithCountResponse
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/29/2017.
 */
interface ClientMainActivityOutputs {
    fun onActiveDamageClaimsResponse(): Observable<DamageClaimsWithCountResponse>
    fun onInactiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>>
}