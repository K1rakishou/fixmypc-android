package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.ClientProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsWithRespondedSpecialistsResponse
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/29/2017.
 */
interface ClientMainActivityOutputs {
    fun onActiveDamageClaimsResponse(): Observable<DamageClaimsWithRespondedSpecialistsResponse>
    fun onInactiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>>
    fun onGetClientProfileResponse(): Observable<ClientProfile>
}