package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.DamageClaimCategory

/**
 * Created by kirakishou on 8/29/2017.
 */
interface ClientNewMalfunctionActivityFragmentCallback : BaseActivityFragmentCallback {
    fun replaceWithFragment(fragmentTag: String)
    fun retrieveCategory(category: DamageClaimCategory)
    fun retrieveDescription(description: String)
    fun retrievePhotos(photos: List<String>)
    fun retrieveLocation(location: LatLng)
    fun onSendPhotosButtonClick()
    fun requestPermission(permission: String, requestCode: Int)
}