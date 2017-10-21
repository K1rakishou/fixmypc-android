package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.adapter.damage_claim

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimResponseCount

/**
 * Created by kirakishou on 9/29/2017.
 */
class DamageClaimGeneric(val damageClaim: DamageClaim,
                         val responsesCount: DamageClaimResponseCount) : DamageClaimListAdapterGenericParam()