package com.kirakishou.fixmypc.fixmypcapp.helper.mapper

import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/12/2017.
 */
class DamageClaimsMapper : Mapper {

    fun mapToDbObject(damageClaim: DamageClaim): DamageClaimEntity {
        return DamageClaimEntity(
                damageClaim.id,
                damageClaim.isActive,
                damageClaim.category,
                damageClaim.description,
                damageClaim.lat,
                damageClaim.lon,
                damageClaim.createdOn,
                TimeUtils.getTimeFast())
    }

    fun mapToDbObjects(damageClaimList: List<DamageClaim>): List<DamageClaimEntity> {
        return damageClaimList.map { mapToDbObject(it) }
    }
}