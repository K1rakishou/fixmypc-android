package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsPhotosMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimPhotoDao
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.Flowable
import timber.log.Timber

/**
 * Created by kirakishou on 9/12/2017.
 */
class DamageClaimRepository(protected val damageClaimDao: DamageClaimDao,
                            protected val damageClaimPhotoDao: DamageClaimPhotoDao,
                            protected val mMapperManager: MapperManager) {

    fun saveAll(damageClaimList: List<DamageClaim>) {
        val damageClaimEntities = mMapperManager.get<DamageClaimsMapper>().mapToEntities(damageClaimList)
        damageClaimDao.saveAll(damageClaimEntities)

        val damageClaimPhotoEntities = mMapperManager.get<DamageClaimsPhotosMapper>().mapToEntities(damageClaimList)
        damageClaimPhotoDao.saveAll(damageClaimPhotoEntities)
    }

    fun getSomeWithinBBox(lat: Double, lon: Double, radius: Double, page: Long): Flowable<List<DamageClaim>> {
        val (maxLatLon, minLatLon) = MathUtils.createBoundingBoxFromPoint(LatLng(lat, lon), radius)

        Timber.d("getSomeWithinBBox() lat: $lat, lon: $lon, radius: $radius, page: $page")
        Timber.d("getSomeWithinBBox() maxLatLon.lat: ${maxLatLon.latitude}, maxLatLon.lon: ${maxLatLon.longitude}, " +
                "minLatLon.lat: ${minLatLon.latitude}, minLatLon.lon: ${minLatLon.longitude}")

        val damageClaimEntityList = damageClaimDao.findSomeWithinBBox(minLatLon.longitude, maxLatLon.longitude,
                minLatLon.latitude, maxLatLon.latitude, page, Constant.MAX_DAMAGE_CLAIMS_PER_PAGE)

        val photoIds = damageClaimEntityList.map { it.id }
        Timber.d("getSomeWithinBBox() found ${photoIds.size} elements,  ids: $photoIds")

        val damageClaimPhotoEntityList = damageClaimPhotoDao.findManyByIds(photoIds)

        val damageClaimList = mMapperManager.get<DamageClaimsMapper>().mapFromEntities(damageClaimEntityList)
        val photosMap = mMapperManager.get<DamageClaimsPhotosMapper>().mapFromEntities(damageClaimPhotoEntityList)

        for (damageClaim in damageClaimList) {
            damageClaim.imageNamesList += photosMap[damageClaim.id]!!
        }

        return Flowable.just(damageClaimList)
    }
}