package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import io.reactivex.Flowable

/**
 * Created by kirakishou on 9/12/2017.
 */

@Dao
interface DamageClaimDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(damageClaimList: List<DamageClaimEntity>): List<Long>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_ENTITY_TABLE_NAME} WHERE " +
            "lon BETWEEN :arg0 AND :arg1 AND " +
            "lat BETWEEN :arg2 AND :arg3 " +
            "ORDER BY saved_on ASC " +
            "LIMIT :arg4 OFFSET :arg5")
    fun findSomeWithinBBox(left: Double, right: Double, bottom: Double, top: Double, count: Long, skip: Long): Flowable<List<DamageClaimEntity>>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_ENTITY_TABLE_NAME} ORDER BY saved_on ASC ")
    fun findAll(): Flowable<List<DamageClaimEntity>>
}