package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import io.reactivex.Flowable

/**
 * Created by kirakishou on 9/12/2017.
 */

@Dao
interface DamageClaimPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(damageClaimPhotoList: List<DamageClaimPhotoEntity>): List<Long>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME} WHERE damage_claim_id IN (:arg0)")
    fun findManyByIds(ids: List<Long>): List<DamageClaimPhotoEntity>

    @Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME}")
    fun findAll(): Flowable<List<DamageClaimPhotoEntity>>

    @Query("DELETE FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME}")
    fun clear()
}