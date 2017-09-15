package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant

/**
 * Created by kirakishou on 9/12/2017.
 */

@Entity(tableName = Constant.Room.TableName.DAMAGE_CLAIM_PHOTO_ENTITY_TABLE_NAME)
data class DamageClaimPhotoEntity(@PrimaryKey(autoGenerate = false)
                                  @ColumnInfo(name = "photo_name")
                                  var photoName: String,

                                  @ColumnInfo(name = "damage_claim_id")
                                  var damageClaimId: Long) {
    constructor() : this("", 0L)
}