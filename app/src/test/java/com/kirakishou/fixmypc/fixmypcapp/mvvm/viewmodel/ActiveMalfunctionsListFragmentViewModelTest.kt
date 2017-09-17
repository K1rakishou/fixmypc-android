package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.repository.DamageClaimRepository
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.TestSchedulers
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by kirakishou on 9/17/2017.
 */

@RunWith(MockitoJUnitRunner::class)
class ActiveMalfunctionsListFragmentViewModelTest {

    @Mock
    lateinit var repository: DamageClaimRepository

    @Mock
    lateinit var apiClient: ApiClient

    @Mock
    lateinit var wifiUtils: WifiUtils

    lateinit var mViewModel: ActiveMalfunctionsListFragmentViewModel

    val damageClaimList1 = mutableListOf(
            DamageClaim(0L, 0L),
            DamageClaim(1L, 0L),
            DamageClaim(2L, 1L),
            DamageClaim(3L, 1L),
            DamageClaim(4L, 2L))

    val damageClaimList2 = mutableListOf(
            DamageClaim(5L, 2L),
            DamageClaim(6L, 3L),
            DamageClaim(7L, 3L),
            DamageClaim(8L, 4L),
            DamageClaim(9L, 4L))

    val notFullDamageClaimList3 = mutableListOf(
            DamageClaim(10L, 5L),
            DamageClaim(11L, 5L),
            DamageClaim(12L, 6L),
            DamageClaim(13L, 6L))

    val fullDamageClaimList3 = mutableListOf(
            DamageClaim(10L, 5L),
            DamageClaim(11L, 5L),
            DamageClaim(12L, 6L),
            DamageClaim(13L, 6L),
            DamageClaim(14L, 7L))

    val fakeResponse1 = DamageClaimsResponse(damageClaimList1, ErrorCode.Remote.REC_OK)
    val fakeResponse2 = DamageClaimsResponse(damageClaimList2, ErrorCode.Remote.REC_OK)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        mViewModel = ActiveMalfunctionsListFragmentViewModel(apiClient, wifiUtils, repository, TestSchedulers())
        mViewModel.init()
    }

    @Test
    fun testViewModel_hasWifiPhoneNotRotated() {
        mViewModel.setIsFirstFragmentStart(true)

        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.getDamageClaims(anyDouble(), anyDouble(), anyDouble(), anyLong(), anyLong()))
                .thenReturn(Single.just(fakeResponse1))
                .thenReturn(Single.just(fakeResponse2))

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 0)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 0L && it[0].damageClaim.ownerId == 0L }
                .assertValue { it[4].damageClaim.id == 4L && it[4].damageClaim.ownerId == 2L }

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 5)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 5L && it[0].damageClaim.ownerId == 2L }
                .assertValue { it[4].damageClaim.id == 9L && it[4].damageClaim.ownerId == 4L }
        verify(repository, never()).findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong())

        mViewModel.mOnDamageClaimsPageReceivedSubject.test().assertValueCount(1)
        mViewModel.mEitherFromRepoOrServerSubject.test().assertNoValues()
        mViewModel.mOnUnknownErrorSubject.test().assertNoValues()
    }

    @Test
    fun testViewModel_noWifiPhoneNotRotated() {
        mViewModel.setIsFirstFragmentStart(true)

        `when`(wifiUtils.isWifiConnected()).thenReturn(false)
        `when`(repository.findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong()))
                .thenReturn(Flowable.just(damageClaimList1))
                .thenReturn(Flowable.just(damageClaimList2))

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 0)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 0L && it[0].damageClaim.ownerId == 0L }
                .assertValue { it[4].damageClaim.id == 4L && it[4].damageClaim.ownerId == 2L }
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 5)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 5L && it[0].damageClaim.ownerId == 2L }
                .assertValue { it[4].damageClaim.id == 9L && it[4].damageClaim.ownerId == 4L }
        verify(repository, times(2)).findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong())
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }
        mViewModel.mOnDamageClaimsPageReceivedSubject.test().assertValueCount(1)
        mViewModel.mOnUnknownErrorSubject.test().assertNoValues()
    }

    @Test
    fun testViewModel_hasWifiPhoneRotated() {
        mViewModel.setIsFirstFragmentStart(false)

        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(repository.findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong()))
                .thenReturn(Flowable.just(damageClaimList1))
                .thenReturn(Flowable.just(damageClaimList2))

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 0)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 0L && it[0].damageClaim.ownerId == 0L }
                .assertValue { it[4].damageClaim.id == 4L && it[4].damageClaim.ownerId == 2L }
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 5)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 5L && it[0].damageClaim.ownerId == 2L }
                .assertValue { it[4].damageClaim.id == 9L && it[4].damageClaim.ownerId == 4L }
        verify(repository, times(2)).findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong())
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }
        mViewModel.mOnDamageClaimsPageReceivedSubject.test().assertValueCount(1)
        mViewModel.mOnUnknownErrorSubject.test().assertNoValues()
    }

    @Test
    fun testViewModel_noWifiPhoneRotated() {
        mViewModel.setIsFirstFragmentStart(false)

        `when`(wifiUtils.isWifiConnected()).thenReturn(false)
        `when`(repository.findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong()))
                .thenReturn(Flowable.just(damageClaimList1))
                .thenReturn(Flowable.just(damageClaimList2))

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 0)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 0L && it[0].damageClaim.ownerId == 0L }
                .assertValue { it[4].damageClaim.id == 4L && it[4].damageClaim.ownerId == 2L }
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 5)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 5L && it[0].damageClaim.ownerId == 2L }
                .assertValue { it[4].damageClaim.id == 9L && it[4].damageClaim.ownerId == 4L }
        verify(repository, times(2)).findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong())
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }
        mViewModel.mOnDamageClaimsPageReceivedSubject.test().assertValueCount(1)
        mViewModel.mOnUnknownErrorSubject.test().assertNoValues()
    }

    @Test
    fun testViewModel_repoReturnedLessThanPageCountItems() {
        mViewModel.setIsFirstFragmentStart(false)

        `when`(wifiUtils.isWifiConnected()).thenReturn(false)
        `when`(repository.findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong()))
                .thenReturn(Flowable.just(notFullDamageClaimList3))
        `when`(apiClient.getDamageClaims(anyDouble(), anyDouble(), anyDouble(), anyLong(), anyLong()))
                .thenReturn(Single.just(DamageClaimsResponse(fullDamageClaimList3, ErrorCode.Remote.REC_OK)))

        mViewModel.getDamageClaimsWithinRadius(LatLng(55.2, 33.6), 1000.0, 0)

        mViewModel.mOutputs.onDamageClaimsPageReceived()
                .test()
                .assertValue { it.size == 5 }
                .assertValue { it[0].damageClaim.id == 10L && it[0].damageClaim.ownerId == 5L }
                .assertValue { it[4].damageClaim.id == 14L && it[4].damageClaim.ownerId == 7L }
        verify(repository, times(1)).findWithinBBox(anyDouble(), anyDouble(), anyDouble(), anyLong())
        verify(apiClient, times(1)).getDamageClaims(anyDouble(), anyDouble(), anyDouble(), anyLong(), anyLong())
        mViewModel.mEitherFromRepoOrServerSubject.test().assertValue { it.second.damageClaims.size == 5 }
        mViewModel.mOnDamageClaimsPageReceivedSubject.test().assertValueCount(1)
        mViewModel.mOnUnknownErrorSubject.test().assertNoValues()
    }
}





















































