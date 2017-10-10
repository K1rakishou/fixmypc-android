package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.NewProfileInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.SpecialistProfilePacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.UpdateSpecialistProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.UpdateSpecialistProfileActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.UpdateSpecialistProfileActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.UpdateSpecialistProfileActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
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

    lateinit var mUpdateSpecialistProfileFragment: BehaviorSubject<NewProfileInfo>
    lateinit var mOnUpdateSpecialistProfileResponseSubject: BehaviorSubject<Unit>
    lateinit var mNewProfileSubject: BehaviorSubject<NewProfileInfo>
    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mCompositeDisposable.clear()

        mUpdateSpecialistProfileFragment = BehaviorSubject.create()
        mOnUpdateSpecialistProfileResponseSubject = BehaviorSubject.create()
        mNewProfileSubject = BehaviorSubject.create()
        mOnBadResponseSubject = BehaviorSubject.create()
        mOnUnknownErrorSubject = BehaviorSubject.create()

        mCompositeDisposable += mNewProfileSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap { newProfileInfo ->
                    val response = mApiClient.updateSpecialistProfile(newProfileInfo.photoPath, SpecialistProfilePacket(newProfileInfo.name, newProfileInfo.phone))
                            .toObservable()

                    return@flatMap Observables.zip(response, Observable.just(newProfileInfo))
                }
                .doOnNext { (response, newProfileInfo) ->
                    if (response.errorCode == ErrorCode.Remote.REC_OK) {
                        newProfileInfo.photoPath = response.newPhotoName
                        mUpdateSpecialistProfileFragment.onNext(newProfileInfo)
                    }
                }
                .subscribe({ (response, _) ->
                    handleResponse(response)
                }, {
                    handleError(it)
                })
    }

    override fun updateSpecialistProfile(photoPath: String, name: String, phone: String) {
        mNewProfileSubject.onNext(NewProfileInfo(photoPath, name, phone))
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
                is UpdateSpecialistProfileResponse -> {
                    mOnUpdateSpecialistProfileResponseSubject.onNext(Unit)
                }
            }
        } else {
            when (response) {
                is UpdateSpecialistProfileResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE,
                        ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED,
                        ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> throw RuntimeException("Unknown errorCode: $errorCode")
                    }
                }
            }
        }
    }

    private fun handleError(error: Throwable) {
        mOnUnknownErrorSubject.onNext(error)
    }

    override fun onUpdateSpecialistProfileFragment(): Observable<NewProfileInfo> = mUpdateSpecialistProfileFragment
    override fun onUpdateSpecialistProfileResponseSubject(): Observable<Unit> = mOnUpdateSpecialistProfileResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}