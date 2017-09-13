package com.kirakishou.fixmypc.fixmypcapp.helper.mapper

import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/12/2017.
 */
class DamageClaimsMapper : Mapper {

    fun mapToEntity(damageClaim: DamageClaim): DamageClaimEntity {
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

    fun mapToEntities(damageClaimList: List<DamageClaim>): List<DamageClaimEntity> {
        return damageClaimList.map { mapToEntity(it) }
    }

    fun mapFromEntity(damageClaimEntity: DamageClaimEntity): DamageClaim {
        return DamageClaim(
                damageClaimEntity.id,
                -1L,
                damageClaimEntity.isActive,
                damageClaimEntity.category,
                damageClaimEntity.description,
                damageClaimEntity.lat,
                damageClaimEntity.lon,
                damageClaimEntity.createdOn,
                emptyList())
    }

    fun mapFromEntities(damageClaimEntityList: List<DamageClaimEntity>): List<DamageClaim> {
        return damageClaimEntityList.map { mapFromEntity(it) }
    }
}