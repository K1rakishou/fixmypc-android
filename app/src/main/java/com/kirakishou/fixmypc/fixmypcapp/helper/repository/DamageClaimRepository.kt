package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsPhotosMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimPhotoDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.database.MyDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import timber.log.Timber

/**
 * Created by kirakishou on 9/12/2017.
 */
open class DamageClaimRepository(protected val mDatabase: MyDatabase,
                                 protected val mMapperManager: MapperManager) {

    private val damageClaimDao: DamageClaimDao by lazy { mDatabase.damageClaimDao() }
    private val damageClaimPhotoDao: DamageClaimPhotoDao by lazy { mDatabase.damageClaimPhotoDao() }

    fun saveAll(damageClaimList: List<DamageClaim>) {
        if (damageClaimList.isEmpty()) {
            return
        }

        mDatabase.runInTransaction {
            Timber.d("saveAll() saving ${damageClaimList.size} damageClaims")

            val damageClaimEntities = mMapperManager.get<DamageClaimsMapper>().mapToEntities(damageClaimList)
            damageClaimDao.saveAll(damageClaimEntities)

            val damageClaimPhotoEntities = mMapperManager.get<DamageClaimsPhotosMapper>().mapToEntities(damageClaimList)
            damageClaimPhotoDao.saveAll(damageClaimPhotoEntities)

            Timber.d("saveAll() ok")
        }
    }

    fun findWithinBBox(lat: Double, lon: Double, radius: Double, page: Long): Flowable<List<DamageClaim>> {
        val (maxLatLon, minLatLon) = MathUtils.createBoundingBoxFromPoint(LatLng(lat, lon), radius)

        Timber.d("findWithinBBox() lat: $lat, lon: $lon, radius: $radius, page: $page")
        Timber.d("findWithinBBox() minLatLon.lon: ${minLatLon.longitude}, maxLatLon.lon: ${maxLatLon.longitude}, " +
                "minLatLon.lat: ${minLatLon.latitude}, maxLatLon.lat: ${maxLatLon.latitude}")

        return damageClaimDao.findSomeWithinBBox(minLatLon.longitude, maxLatLon.longitude,
                minLatLon.latitude, maxLatLon.latitude, Constant.MAX_DAMAGE_CLAIMS_PER_PAGE, page * Constant.MAX_DAMAGE_CLAIMS_PER_PAGE)
                .map { damageClaimEntityList ->
                    val photoIds = damageClaimEntityList.map { it.id }
                    val damageClaimPhotoEntityList = damageClaimPhotoDao.findManyByIds(photoIds)

                    damageClaimDao.findAll().blockingFirst().forEach { println(it) }
                    damageClaimEntityList.forEach { println("description = ${it.description}") }

                    val damageClaimList = mMapperManager.get<DamageClaimsMapper>().mapFromEntities(damageClaimEntityList)
                    val photosMap = mMapperManager.get<DamageClaimsPhotosMapper>().mapFromEntities(damageClaimPhotoEntityList)

                    if (photosMap.isEmpty()) {
                        return@map damageClaimList
                    }

                    for (damageClaim in damageClaimList) {
                        damageClaim.imageNamesList += photosMap[damageClaim.id]!!
                    }

                    return@map damageClaimList
                }
    }

    fun findAll(): Flowable<List<DamageClaim>> {
        val damageClaimFlowable = damageClaimDao
                .findAll()
                .map { mMapperManager.get<DamageClaimsMapper>().mapFromEntities(it) }

        val damageClaimPhotoFlowable = damageClaimPhotoDao
                .findAll()
                .map { mMapperManager.get<DamageClaimsPhotosMapper>().mapFromEntities(it) }

        return Flowables.zip(damageClaimFlowable, damageClaimPhotoFlowable, { dcf, dcpf ->
            dcf.forEach {
                if (dcpf.containsKey(it.id)) {
                    it.imageNamesList += dcpf[it.id]!!
                }
            }

            return@zip dcf
        })
    }
}










































