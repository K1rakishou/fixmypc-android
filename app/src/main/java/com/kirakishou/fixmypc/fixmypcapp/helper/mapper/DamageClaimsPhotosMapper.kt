package com.kirakishou.fixmypc.fixmypcapp.helper.mapper

import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/12/2017.
 */
class DamageClaimsPhotosMapper : Mapper {

    fun mapToEntity(damageClaim: DamageClaim): List<DamageClaimPhotoEntity> {
        return (0 until damageClaim.imageNamesList.size)
                .map { DamageClaimPhotoEntity(null, damageClaim.id,  damageClaim.imageNamesList[it]) }
    }

    fun mapToEntities(damageClaimList: List<DamageClaim>): List<DamageClaimPhotoEntity> {
        return damageClaimList.flatMap { mapToEntity(it) }
    }

    fun mapFromEntity(damageClaimPhotoEntity: DamageClaimPhotoEntity): Pair<Long, String> {
        return damageClaimPhotoEntity.damageClaimId to damageClaimPhotoEntity.photoName
    }

    fun mapFromEntities(damageClaimPhotoEntityList: List<DamageClaimPhotoEntity>): Map<Long, List<String>> {
        val photosById = damageClaimPhotoEntityList
                .map { mapFromEntity(it) }

        val photoMap = hashMapOf<Long, ArrayList<String>>()

        for ((id, name) in photosById) {
            if (!photoMap.containsKey(id)) {
                photoMap.put(id, ArrayList())
            }

            photoMap[id]!!.add(name)
        }

        return photoMap
    }
}