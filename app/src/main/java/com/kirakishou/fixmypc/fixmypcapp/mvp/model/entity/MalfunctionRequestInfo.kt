package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.MalfunctionCategory

/**
 * Created by kirakishou on 8/1/2017.
 */
class MalfunctionRequestInfo {
    var malfunctionCategory: MalfunctionCategory = MalfunctionCategory.Computer
    var malfunctionDescription: String = ""
    var malfunctionPhotos: ArrayList<String> = arrayListOf()
    var malfunctionLocation: LatLng = LatLng(0.0, 0.0)
}