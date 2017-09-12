package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity

/**
 * Created by kirakishou on 9/12/2017.
 */

@Dao
interface DamageClaimPhotoDao {

    @Insert
    fun saveAll(damageClaimPhotoList: List<DamageClaimPhotoEntity>): List<Long>

    /*@Query("SELECT * FROM ${Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME} WHERE id IN :ids")
    fun findManyByIds(ids: Array<Long>): List<DamageClaimPhotoEntity>*/
}