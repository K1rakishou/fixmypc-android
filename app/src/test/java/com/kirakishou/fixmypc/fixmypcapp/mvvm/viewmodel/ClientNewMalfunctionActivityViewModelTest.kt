package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.TestSchedulers
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by kirakishou on 9/17/2017.
 */

@RunWith(MockitoJUnitRunner::class)
class ClientNewMalfunctionActivityViewModelTest {

    @Mock
    lateinit var apiClient: ApiClient

    @Mock
    lateinit var wifiUtils: WifiUtils

    lateinit var mViewModel: ClientNewDamageClaimActivityViewModel

    fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        mViewModel = ClientNewDamageClaimActivityViewModel(apiClient, wifiUtils, TestSchedulers())
        mViewModel.init()
    }

    private fun initDamageClaimObject() {
        mViewModel.setCategory(DamageClaimCategory.Phone)
        mViewModel.setDescription("123")
        mViewModel.setLocation(LatLng(55.3, 44.2))
        mViewModel.setPhotos(listOf())
    }

    @Test
    fun testViewModel_noWifi() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(false)

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onWifiNotConnected().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedFileSizeExceededErrorCode() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onFileSizeExceeded().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedRequestSizeExceededErrorCode() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onRequestSizeExceeded().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedAllFileServersAreNotWorkingErrorCode() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onAllFileServersAreNotWorking().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedDatabaseErrorErrorCode() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_DATABASE_ERROR)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onServerDatabaseError().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenTimeout() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_TIMEOUT)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onCouldNotConnectToServer().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenCouldNotConnectToServer() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onCouldNotConnectToServer().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenSelectedPhotoDoesNotExists() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onSelectedPhotoDoesNotExists().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenResponseBodyIsEmpty() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onResponseBodyIsEmpty().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenFileSelectedTwice() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onFileAlreadySelected().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedBadResponse() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onBadServerResponse().test().assertValueCount(1)
    }

    @Test
    fun testViewModel_whenServerReturnedBadFileExtension() {
        initDamageClaimObject()
        `when`(wifiUtils.isWifiConnected()).thenReturn(true)
        `when`(apiClient.createMalfunctionRequest(anyObject(), anyObject()))
                .thenReturn(Single.just(StatusResponse(ErrorCode.Remote.REC_BAD_ORIGINAL_FILE_NAME)))

        mViewModel.sendMalfunctionRequestToServer(false)

        mViewModel.mErrors.onBadOriginalFileNameSubject().test().assertValueCount(1)
    }
}
















































