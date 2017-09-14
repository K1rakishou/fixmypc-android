package com.kirakishou.fixmypc.fixmypcapp.helper.repository

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kirakishou.fixmypc.fixmypcapp.helper.mapper.MapperManager
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.database.MyDatabase
import com.kirakishou.fixmypc.fixmypcapp.helper.util.TimeUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by kirakishou on 9/14/2017.
 */
@RunWith(AndroidJUnit4::class)
class DamageClaimRepositoryTest {

    private lateinit var mDatabase: MyDatabase
    private lateinit var mMapperManager: MapperManager
    private lateinit var mRepository: DamageClaimRepository

    private val damageClaimList = listOf(
            DamageClaim(0L, 0L, true, 0, "test1", 55.1, 36.1, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(1L, 0L, true, 1, "test2", 55.2, 36.2, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(2L, 0L, true, 2, "test3", 55.3, 36.3, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(3L, 0L, true, 0, "test4", 55.4, 36.4, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(4L, 0L, true, 1, "test5", 55.5, 36.5, TimeUtils.getTimeFast(), emptyList()),

            DamageClaim(5L, 1L, true, 2, "test6", 55.6, 36.6, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(6L, 1L, true, 0, "test7", 55.7, 36.7, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(7L, 1L, true, 1, "test8", 55.8, 36.8, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(8L, 1L, true, 2, "test9", 55.9, 36.9, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(9L, 1L, true, 0, "test10", 55.0, 36.0, TimeUtils.getTimeFast(), emptyList()),

            DamageClaim(10L, 2L, true, 1, "test11", 55.15, 36.15, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(11L, 2L, true, 2, "test12", 55.25, 36.25, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(12L, 2L, true, 0, "test13", 55.35, 36.35, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(13L, 2L, true, 1, "test14", 55.45, 36.45, TimeUtils.getTimeFast(), emptyList()),
            DamageClaim(14L, 2L, true, 2, "test15", 55.55, 36.55, TimeUtils.getTimeFast(), emptyList()))

    @Before
    fun init() {
        mDatabase =  Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), MyDatabase::class.java).build()
        mMapperManager = MapperManager()
        mRepository = DamageClaimRepository(mDatabase, mMapperManager)
    }

    @After
    fun tearDown() {
        mDatabase.close()
    }

    @Test
    fun testSaveAll() {
        mRepository.saveAll(damageClaimList)

        val damageClaimListFromRepo = mRepository.findAll().blockingFirst()

        assertEquals(15, damageClaimListFromRepo.size)
        assertEquals(0L, damageClaimListFromRepo[0].id)
        assertEquals(14L, damageClaimListFromRepo[14].id)
    }

    @Test
    fun testReplace() {
        val damageClaim = DamageClaim(0L, 0L, true, 0, "test1", 55.1, 36.1, TimeUtils.getTimeFast(), emptyList())

        mRepository.saveAll(listOf(damageClaim))

        val result1 = mRepository.findAll().blockingFirst()
        assertEquals(1, result1.size)
        assertEquals(0L, result1[0].id)

        damageClaim.description = "new_description"

        mRepository.saveAll(listOf(damageClaim))

        val result2 = mRepository.findAll().blockingFirst()
        assertEquals(1, result2.size)
        assertEquals("new_description", result2[0].description)
    }

    @Test
    fun findWithinBBox() {
        mRepository.saveAll(damageClaimList)

        val firstPage = mRepository.findWithinBBox(55.5, 36.5, 1000.0, 0).blockingFirst()

        assertEquals(Constant.MAX_DAMAGE_CLAIMS_PER_PAGE, firstPage.size.toLong())
        assertEquals("test1", firstPage[0].description)
        assertEquals("test5", firstPage[4].description)

        val secondPage = mRepository.findWithinBBox(55.5, 36.5, 1000.0, 1).blockingFirst()

        assertEquals(Constant.MAX_DAMAGE_CLAIMS_PER_PAGE, firstPage.size.toLong())
        assertEquals("test6", secondPage[0].description)
        assertEquals("test10", secondPage[4].description)

        val thirdPage = mRepository.findWithinBBox(55.5, 36.5, 1000.0, 2).blockingFirst()

        assertEquals(Constant.MAX_DAMAGE_CLAIMS_PER_PAGE, firstPage.size.toLong())
        assertEquals("test11", thirdPage[0].description)
        assertEquals("test15", thirdPage[4].description)
    }
}





























