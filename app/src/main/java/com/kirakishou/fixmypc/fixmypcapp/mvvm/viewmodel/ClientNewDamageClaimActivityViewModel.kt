package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.helper.wifi.WifiUtils
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error.ClientNewDamageClaimActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input.ClientNewDamageClaimActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output.ClientNewDamageClaimActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
class ClientNewDamageClaimActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mWifiUtils: WifiUtils,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        ClientNewDamageClaimActivityInputs,
        ClientNewDamageClaimActivityOutputs,
        ClientNewDamageClaimActivityErrors {

    val mInputs: ClientNewDamageClaimActivityInputs = this
    val mOutputs: ClientNewDamageClaimActivityOutputs = this
    val mErrors: ClientNewDamageClaimActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()
    private val damageClaimRequestInfo = DamageClaimInfo()

    private val mOnBadResponseSubject = PublishSubject.create<ErrorCode.Remote>()
    private val mSendMalfunctionRequestToServerSubject = PublishSubject.create<DamageClaimInfo>()
    private val mOnMalfunctionRequestSuccessfullyCreatedSubject = PublishSubject.create<Unit>()
    private val mOnUnknownErrorSubject = PublishSubject.create<Throwable>()

    init {
        //if wifi connected - send request to server
        mCompositeDisposable += mSendMalfunctionRequestToServerSubject
                .filter { _ -> mWifiUtils.isWifiConnected() }
                .observeOn(mSchedulers.provideMain())
                .observeOn(mSchedulers.provideIo())
                .flatMap { mApiClient.createMalfunctionRequest(it).toObservable() }
                .observeOn(mSchedulers.provideMain())
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        //if wifi not connected - notify user about that
        mCompositeDisposable += mSendMalfunctionRequestToServerSubject
                .filter { _ -> !mWifiUtils.isWifiConnected() }
                .observeOn(mSchedulers.provideMain())
                .subscribe({
                    handleResponse(StatusResponse(ErrorCode.Remote.REC_WIFI_IS_NOT_CONNECTED))
                }, {
                    handleError(it)
                })
    }

    override fun onCleared() {
        Timber.e("ClientNewMalfunctionActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    fun setCategory(category: DamageClaimCategory) {
        damageClaimRequestInfo.damageClaimCategory = category
    }

    fun setDescription(description: String) {
        damageClaimRequestInfo.damageClaimDescription = description
    }

    fun setPhotos(photos: List<String>) {
        damageClaimRequestInfo.damageClaimPhotos = ArrayList(photos)
    }

    fun setLocation(location: LatLng) {
        damageClaimRequestInfo.damageClaimLocation = location
    }

    fun getDamageClaimRequestInfo(): DamageClaimInfo {
        return damageClaimRequestInfo
    }

    override fun sendMalfunctionRequestToServer(checkWifiStatus: Boolean) {
        mSendMalfunctionRequestToServerSubject.onNext(damageClaimRequestInfo)
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            handleBadResponse(response.errorCode)
            return
        }

        mOnMalfunctionRequestSuccessfullyCreatedSubject.onNext(Unit)
    }

    private fun handleBadResponse(errorCode: ErrorCode.Remote) {
        when (errorCode) {
            ErrorCode.Remote.REC_NO_PHOTOS_WERE_SELECTED_TO_UPLOAD,
            ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED,
            ErrorCode.Remote.REC_WIFI_IS_NOT_CONNECTED,
            ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED,
            ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED,
            ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING,
            ErrorCode.Remote.REC_DATABASE_ERROR,
            ErrorCode.Remote.REC_TIMEOUT,
            ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
            ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS,
            ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY,
            ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION,
            ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
            ErrorCode.Remote.REC_BAD_ORIGINAL_FILE_NAME,
            ErrorCode.Remote.REC_COULD_NOT_FIND_PROFILE,
            ErrorCode.Remote.REC_PROFILE_IS_NOT_FILLED_IN -> {
                mOnBadResponseSubject.onNext(errorCode)
            }

            else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
        }
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)
        mOnUnknownErrorSubject.onNext(error)
    }

    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onMalfunctionRequestSuccessfullyCreated(): Observable<Unit> = mOnMalfunctionRequestSuccessfullyCreatedSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}