package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsPhotosMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimPhotoDao
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim

/**
 * Created by kirakishou on 9/12/2017.
 */
class DamageClaimRepository(protected val damageClaimDao: DamageClaimDao,
                            protected val damageClaimPhotoDao: DamageClaimPhotoDao,
                            protected val mMapperManager: MapperManager) {

    fun saveAll(damageClaimList: List<DamageClaim>) {
        val damageClaimEntities = mMapperManager.get<DamageClaimsMapper>().mapToDbObjects(damageClaimList)
        damageClaimDao.saveAll(damageClaimEntities)

        val damageClaimPhotoEntities = mMapperManager.get<DamageClaimsPhotosMapper>().mapToDbObjects(damageClaimList)
        damageClaimPhotoDao.saveAll(damageClaimPhotoEntities)
    }
}