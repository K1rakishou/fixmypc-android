package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentView

/**
 * Created by kirakishou on 9/3/2017.
 */
abstract class ActiveMalfunctionsListFragmentPresenter<V: BaseFragmentView> : BaseFragmentPresenter<V>() {
    abstract fun getDamageClaimsWithinRadius(latLng: LatLng, radius: Double, page: Long)
}