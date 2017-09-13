package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant

/**
 * Created by kirakishou on 9/12/2017.
 */

@Dao
interface DamageClaimPhotoDao {

    @Insert
    fun saveAll(damageClaimPhotoList: List<DamageClaimPhotoEntity>): List<Long>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME} WHERE id IN (:arg0)")
    fun findManyByIds(ids: List<Long>): List<DamageClaimPhotoEntity>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME}")
    fun findAll(): List<DamageClaimPhotoEntity>
}