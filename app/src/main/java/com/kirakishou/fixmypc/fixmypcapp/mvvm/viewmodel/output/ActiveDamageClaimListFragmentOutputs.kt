package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.DamageClaimsWithDistanceDTO
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ActiveDamageClaimListFragmentOutputs {
    fun onDamageClaimsPageReceived(): Observable<ArrayList<DamageClaimsWithDistanceDTO>>
}