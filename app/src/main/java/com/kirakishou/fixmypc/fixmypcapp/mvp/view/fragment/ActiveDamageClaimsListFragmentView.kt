package com.kirakishou.fixmypc.fixmypcapp.mvp.view.fragment

import com.kirakishou.fixmypc.fixmypcapp.base.BaseFragmentView
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.DamageClaimsWithDistanceDTO

/**
 * Created by kirakishou on 9/3/2017.
 */
interface ActiveDamageClaimsListFragmentView : BaseFragmentView {
    fun onDamageClaimsPageReceived(damageClaimList: ArrayList<DamageClaimsWithDistanceDTO>)
}