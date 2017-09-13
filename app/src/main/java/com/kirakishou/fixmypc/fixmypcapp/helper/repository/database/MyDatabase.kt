package com.kirakishou.fixmypc.fixmypcapp.helper.repository.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.DamageClaimPhotoDao
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimEntity
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.dao.entity.DamageClaimPhotoEntity

/**
 * Created by kirakishou on 9/12/2017.
 */

@Database(entities = arrayOf(
        DamageClaimEntity::class,
        DamageClaimPhotoEntity::class), version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun damageClaimDao(): DamageClaimDao
    abstract fun damageClaimPhotoDao(): DamageClaimPhotoDao

    fun runInTransaction(func: () -> Unit) {
        this.beginTransaction()

        try {
            func()
            this.setTransactionSuccessful()
        } finally {
            this.endTransaction()
        }
    }
}