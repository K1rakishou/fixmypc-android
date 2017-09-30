package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/29/2017.
 */
interface ClientMainActivityOutputs {
    fun onActiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>>
    fun onInactiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>>
    fun mOnSpecialistsListResponse(): Observable<List<SpecialistProfile>>
}