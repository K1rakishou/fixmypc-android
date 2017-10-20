package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.NewProfileInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.ClientProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.ClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateClientProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error.UpdateClientProfileActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input.UpdateClientProfileActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output.UpdateClientProfileActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/20/2017.
 */
class UpdateClientProfileActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        UpdateClientProfileActivityInputs,
        UpdateClientProfileActivityOutputs,
        UpdateClientProfileActivityErrors {

    val mInputs: UpdateClientProfileActivityInputs = this
    val mOutputs: UpdateClientProfileActivityOutputs = this
    val mErrors: UpdateClientProfileActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    private val mOnUpdateProfileInfoResponseSubject = PublishSubject.create<Unit>()
    private val mUpdateClientProfileFragmentUiInfoSubject = PublishSubject.create<ClientProfilePacket>()
    private val mUpdateProfileInfoSubject = PublishSubject.create<ClientProfilePacket>()
    private val mOnBadResponseSubject = PublishSubject.create<ErrorCode.Remote>()
    private val mOnUnknownErrorSubject = PublishSubject.create<Throwable>()

    override fun onCleared() {
        Timber.e("ClientMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    init {
        mCompositeDisposable += mUpdateProfileInfoSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap { packet ->
                    val response = mApiClient.updateClientProfile(packet)
                        .toObservable()

                    return@flatMap Observables.zip(response, Observable.just(packet))
                }
                .doOnNext { (response, newProfileInfo) ->
                    if (response.errorCode == ErrorCode.Remote.REC_OK) {
                        mUpdateClientProfileFragmentUiInfoSubject.onNext(newProfileInfo)
                    }
                }
                .map { it.first }
                .subscribe({ response ->
                    handleResponse(response)
                }, {
                    handleError(it)
                })
    }

    override fun updateProfileInfoSubject(profileName: String, profilePhone: String) {
        mUpdateProfileInfoSubject.onNext(ClientProfilePacket(profileName, profilePhone))
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is UpdateClientProfileResponse -> {
                    mOnUpdateProfileInfoResponseSubject.onNext(Unit)
                }
            }
        } else {
            when (response) {
                is UpdateClientProfileResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
                    }
                }
            }
        }
    }

    private fun handleError(error: Throwable) {
        mOnUnknownErrorSubject.onNext(error)
    }

    override fun onUpdateProfileInfoResponse(): Observable<Unit> = mOnUpdateProfileInfoResponseSubject
    override fun onUpdateClientProfileFragmentUiInfo(): Observable<ClientProfilePacket> = mUpdateClientProfileFragmentUiInfoSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}









































