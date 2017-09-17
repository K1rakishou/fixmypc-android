package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import io.reactivex.Flowable

/**
 * Created by kirakishou on 9/17/2017.
 */
interface DamageClaimRepository {
    fun saveAll(damageClaimList: List<DamageClaim>)
    fun findWithinBBox(lat: Double, lon: Double, radius: Double, skip: Long): Flowable<List<DamageClaim>>
    fun findAll(): Flowable<List<DamageClaim>>
}