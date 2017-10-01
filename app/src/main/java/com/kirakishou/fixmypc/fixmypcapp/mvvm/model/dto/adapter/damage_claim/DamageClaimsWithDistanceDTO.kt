package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/7/2017.
 */
open class DamageClaimsWithDistanceDTO(val distance: Double,
                                       val damageClaim: DamageClaim) : DamageClaimListAdapterGenericParam()