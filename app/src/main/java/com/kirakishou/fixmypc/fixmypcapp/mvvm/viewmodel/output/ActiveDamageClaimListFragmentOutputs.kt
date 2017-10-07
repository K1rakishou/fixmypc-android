package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim.DamageClaimsWithDistanceDTO
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ActiveDamageClaimListFragmentOutputs {
    fun onDamageClaimsPageReceived(): Observable<ArrayList<DamageClaimsWithDistanceDTO>>
    fun onRespondToDamageClaimSuccessSubject(): Observable<Unit>
    fun onHasAlreadyRespondedResponse(): Observable<Boolean>
    fun onSpecialistProfileResponseSubject(): Observable<SpecialistProfile>
}