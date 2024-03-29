package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimsWithDistance
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface SpecialistMainActivityOutputs {
    fun onDamageClaimsPageReceived(): Observable<ArrayList<DamageClaimsWithDistance>>
    fun onSpecialistProfileResponseSubject(): Observable<SpecialistProfile>
}