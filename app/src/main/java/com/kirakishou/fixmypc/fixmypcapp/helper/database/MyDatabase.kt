package com.kirakishou.fixmypc.fixmypcapp.helper.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.dao.DamageClaimEntity

/**
 * Created by kirakishou on 9/12/2017.
 */

@Database(entities = arrayOf(DamageClaimEntity::class), version = 1)
abstract class MyDatabase : RoomDatabase()