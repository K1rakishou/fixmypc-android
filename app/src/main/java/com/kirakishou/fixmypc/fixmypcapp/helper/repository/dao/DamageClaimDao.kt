package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import io.reactivex.Flowable

/**
 * Created by kirakishou on 9/12/2017.
 */

@Dao
interface DamageClaimDao {

    @Insert
    fun saveAll(damageClaimList: List<DamageClaimEntity>)

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_ENTITY_TABLE_NAME} ORDER BY created_on ASC OFFSET :page LIMIT :count")
    fun findSome(page: Long, count: Long): Flowable<List<DamageClaimEntity>>
}