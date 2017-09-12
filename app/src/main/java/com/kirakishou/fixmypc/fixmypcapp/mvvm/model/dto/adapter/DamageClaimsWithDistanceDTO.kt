package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/7/2017.
 */
open class DamageClaimsWithDistanceDTO(val distance: Double,
                                       val damageClaim: DamageClaim) : DamageClaimListAdapterGenericParam()