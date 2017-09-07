package com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/7/2017.
 */
data class DamageClaimsWithDistanceDTO(val distance: Double,
                                       val damageClaim: DamageClaim)