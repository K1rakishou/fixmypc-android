package com.kirakishou.fixmypc.fixmypcapp.helper.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity

/**
 * Created by kirakishou on 9/12/2017.
 */

@Database(entities = arrayOf(
        DamageClaimEntity::class,
        DamageClaimPhotoEntity::class), version = 1)
abstract class MyDatabase : RoomDatabase()