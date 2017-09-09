package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory

/**
 * Created by kirakishou on 8/1/2017.
 */
class DamageClaimInfo {
    var damageClaimCategory: DamageClaimCategory = DamageClaimCategory.Computer
    var damageClaimDescription: String = ""
    var damageClaimPhotos: ArrayList<String> = arrayListOf()
    var damageClaimLocation: LatLng = LatLng(0.0, 0.0)
}