package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input

import com.google.android.gms.maps.model.LatLng

/**
 * Created by kirakishou on 9/9/2017.
 */
interface SpecialistMainActivityInputs {
    fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long)
    fun checkHasAlreadyRespondedToDamageClaim(damageClaimId: Long)
    fun respondToDamageClaim(damageClaimId: Long)
    fun getSpecialistProfile()
    fun updateSpecialistProfile(photoPath: String, name: String, phone: String)
}