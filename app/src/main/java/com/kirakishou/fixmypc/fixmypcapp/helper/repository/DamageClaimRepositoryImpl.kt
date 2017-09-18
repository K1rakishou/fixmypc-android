package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.DamageClaimsPhotosMapper
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimPhotoDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.database.MyDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.util.MathUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables

/**
 * Created by kirakishou on 9/17/2017.
 */
class DamageClaimRepositoryImpl(protected val mDatabase: MyDatabase,
                                protected val mMapperManager: MapperManager,
                                protected val mSchedulers: SchedulerProvider) : DamageClaimRepository {

    private val damageClaimDao: DamageClaimDao by lazy { mDatabase.damageClaimDao() }
    private val damageClaimPhotoDao: DamageClaimPhotoDao by lazy { mDatabase.damageClaimPhotoDao() }

    override fun saveAll(damageClaimList: List<DamageClaim>) {
        if (damageClaimList.isEmpty()) {
            return
        }

        mDatabase.runInTransaction {
            val damageClaimEntities = mMapperManager.get<DamageClaimsMapper>().mapToEntities(damageClaimList)
            damageClaimDao.saveAll(damageClaimEntities)

            val damageClaimPhotoEntities = mMapperManager.get<DamageClaimsPhotosMapper>().mapToEntities(damageClaimList)
            damageClaimPhotoDao.saveAll(damageClaimPhotoEntities)
        }
    }

    override fun findWithinBBox(lat: Double, lon: Double, radius: Double, skip: Long): Flowable<List<DamageClaim>> {
        val (maxLatLon, minLatLon) = MathUtils.createBoundingBoxFromPoint(LatLng(lat, lon), radius)

        return damageClaimDao.findSomeWithinBBox(minLatLon.longitude, maxLatLon.longitude,
                minLatLon.latitude, maxLatLon.latitude, Constant.MAX_DAMAGE_CLAIMS_PER_PAGE, skip)
                .subscribeOn(mSchedulers.provideIo())
                .map { damageClaimEntityList ->
                    val photoIds = damageClaimEntityList.map { it.id }
                    val damageClaimPhotoEntityList = damageClaimPhotoDao.findManyByIds(photoIds)

                    val damageClaimList = mMapperManager.get<DamageClaimsMapper>().mapFromEntities(damageClaimEntityList)
                    val photosMap = mMapperManager.get<DamageClaimsPhotosMapper>().mapFromEntities(damageClaimPhotoEntityList)

                    if (photosMap.isEmpty()) {
                        return@map damageClaimList
                    }

                    for (damageClaim in damageClaimList) {
                        damageClaim.photoNames += photosMap[damageClaim.id]!!
                    }

                    return@map damageClaimList
                }
    }

    override fun findAll(): Flowable<List<DamageClaim>> {
        val damageClaimFlowable = damageClaimDao
                .findAll()
                .subscribeOn(mSchedulers.provideIo())
                .map { mMapperManager.get<DamageClaimsMapper>().mapFromEntities(it) }

        val damageClaimPhotoFlowable = damageClaimPhotoDao
                .findAll()
                .subscribeOn(mSchedulers.provideIo())
                .map { mMapperManager.get<DamageClaimsPhotosMapper>().mapFromEntities(it) }

        return Flowables.zip(damageClaimFlowable, damageClaimPhotoFlowable, { dcf, dcpf ->
            dcf.forEach {
                if (dcpf.containsKey(it.id)) {
                    it.photoNames += dcpf[it.id]!!
                }
            }

            return@zip dcf
        }).subscribeOn(mSchedulers.provideIo())
    }
}