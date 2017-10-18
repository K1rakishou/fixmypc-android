package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.NewProfileInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.SpecialistProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfileInfoResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfilePhotoResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.UpdateSpecialistProfileActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.UpdateSpecialistProfileActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.UpdateSpecialistProfileActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/10/2017.
 */
class UpdateSpecialistProfileActivityViewModel
@Inject constructor(val mApiClient: ApiClient,
                    val mSchedulers: SchedulerProvider) : BaseViewModel(),
        UpdateSpecialistProfileActivityInputs,
        UpdateSpecialistProfileActivityOutputs,
        UpdateSpecialistProfileActivityErrors {

    val mInputs: UpdateSpecialistProfileActivityInputs = this
    val mOutputs: UpdateSpecialistProfileActivityOutputs = this
    val mErrors: UpdateSpecialistProfileActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    private val mUpdateSpecialistProfileFragmentUiInfoSubject = PublishSubject.create<NewProfileInfo>()
    private val mUpdateSpecialistProfileFragmentUiPhotoSubject = PublishSubject.create<String>()
    private val mUpdateProfilePhotoSubject = PublishSubject.create<String>()
    private val mUpdateProfileInfoSubject = PublishSubject.create<NewProfileInfo>()
    private val mOnUpdateSpecialistProfileResponseSubject = PublishSubject.create<Unit>()
    private val mOnBadResponseSubject = PublishSubject.create<ErrorCode.Remote>()
    private val mOnUnknownErrorSubject = PublishSubject.create<Throwable>()

    fun init() {
        mCompositeDisposable += mUpdateProfileInfoSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap {
                    val response = mApiClient.updateSpecialistProfileInfo(SpecialistProfilePacket(it.name, it.phone))
                            .toObservable()
                    
                    return@flatMap Observables.zip(response, Observable.just(it))
                }
                .doOnNext { (response, newProfileInfo) ->
                    if (response.errorCode == ErrorCode.Remote.REC_OK) {
                        mUpdateSpecialistProfileFragmentUiInfoSubject.onNext(newProfileInfo)
                    }
                }
                .map { it.first }
                .subscribe({ response ->
                    handleResponse(response)
                }, {
                    handleError(it)
                })

        mCompositeDisposable += mUpdateProfilePhotoSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap { mApiClient.updateSpecialistProfilePhoto(it).toObservable() }
                .doOnNext { response ->
                    if (response.errorCode == ErrorCode.Remote.REC_OK) {
                        mUpdateSpecialistProfileFragmentUiPhotoSubject.onNext(response.newPhotoName)
                    }
                }
                .subscribe({ response ->
                    handleResponse(response)
                }, {
                    handleError(it)
                })
    }

    override fun updateSpecialistProfileInfo(name: String, phone: String) {
        mUpdateProfileInfoSubject.onNext(NewProfileInfo(name, phone))
    }

    override fun updateSpecialistProfilePhoto(photoPath: String) {
        mUpdateProfilePhotoSubject.onNext(photoPath)
    }

    override fun onCleared() {
        Timber.e("UpdateSpecialistProfileActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is UpdateSpecialistProfilePhotoResponse -> {
                    mOnUpdateSpecialistProfileResponseSubject.onNext(Unit)
                }

                is UpdateSpecialistProfileInfoResponse -> {
                    mOnUpdateSpecialistProfileResponseSubject.onNext(Unit)
                }
            }
        } else {
            when (response) {
                is UpdateSpecialistProfilePhotoResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE,
                        ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED,
                        ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
                    }
                }

                is UpdateSpecialistProfileInfoResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE,
                        ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED,
                        ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS -> {
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

    override fun onUpdateSpecialistProfileFragmentPhoto(): Observable<String> = mUpdateSpecialistProfileFragmentUiPhotoSubject
    override fun onUpdateSpecialistProfileFragmentInfo(): Observable<NewProfileInfo> = mUpdateSpecialistProfileFragmentUiInfoSubject
    override fun onUpdateSpecialistProfileResponseSubject(): Observable<Unit> = mOnUpdateSpecialistProfileResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}