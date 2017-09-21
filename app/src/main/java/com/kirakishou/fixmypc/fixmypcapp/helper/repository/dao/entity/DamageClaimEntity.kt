package com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant

/**
 * Created by kirakishou on 9/12/2017.
 */

@Entity(tableName = Constant.Room.TableName.DAMAGE_CLAIM_ENTITY_TABLE_NAME)
data class DamageClaimEntity(@PrimaryKey
                             @ColumnInfo(name = "id")
                             var id: Long,

                             @ColumnInfo(name = "owner_id")
                             var ownerId: Long,

                             @ColumnInfo(name = "is_active")
                             var isActive: Boolean,

                             @ColumnInfo(name = "category")
                             var category: Int,

                             @ColumnInfo(name = "description")
                             var description: String,

                             @ColumnInfo(name = "lat")
                             var lat: Double,

                             @ColumnInfo(name = "lon")
                             var lon: Double,

                             @ColumnInfo(name = "created_on")
                             var createdOn: Long,

                             @ColumnInfo(name = "saved_on")
                             var savedOn: Long) {

    constructor() : this(0L, 0L, false, 0, "", 0.0, 0.0, 0L, 0L)
}