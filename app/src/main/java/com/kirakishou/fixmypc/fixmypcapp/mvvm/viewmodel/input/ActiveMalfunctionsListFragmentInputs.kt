package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input

import com.google.android.gms.maps.model.LatLng
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ActiveMalfunctionsListFragmentInputs {
    fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long)
    fun isFirstFragmentStartSubject(): BehaviorSubject<Boolean>
}