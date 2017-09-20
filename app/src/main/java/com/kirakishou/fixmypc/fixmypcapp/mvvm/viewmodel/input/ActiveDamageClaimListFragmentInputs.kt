package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input

import com.google.android.gms.maps.model.LatLng

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ActiveDamageClaimListFragmentInputs {
    fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long)
    fun getClientProfile(userId: Long)
}