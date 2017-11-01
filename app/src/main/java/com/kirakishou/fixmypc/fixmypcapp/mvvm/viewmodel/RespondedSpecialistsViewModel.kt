package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.AssignSpecialistPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.AssignSpecialistResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.SpecialistsListResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error.RespondedSpecialistsActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input.RespondedSpecialistsActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output.RespondedSpecialistsActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/2/2017.
 */
class RespondedSpecialistsViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        RespondedSpecialistsActivityInputs,
        RespondedSpecialistsActivityOutputs,
        RespondedSpecialistsActivityErrors {

    val mInputs: RespondedSpecialistsActivityInputs = this
    val mOutputs: RespondedSpecialistsActivityOutputs = this
    val mErrors: RespondedSpecialistsActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    private val mDamageClaimAlreadyHasAssignedSpecialistSubject = PublishSubject.create<Long>()
    private val mOnAssignSpecialistResponseSubject = PublishSubject.create<AssignSpecialistResponse>()
    private val mAssignSpecialistSubject = PublishSubject.create<AssignSpecialistPacket>()
    private val mGetRespondedSpecialistsSubject = PublishSubject.create<GetRespondedSpecialistsDTO>()
    private val mOnSpecialistsListResponseSubject = PublishSubject.create<List<SpecialistProfile>>()
    private val mOnBadResponseSubject = PublishSubject.create<ErrorCode.Remote>()
    private val mOnUnknownErrorSubject = PublishSubject.create<Throwable>()

    init {
        val respondedSpecialistObservable = mGetRespondedSpecialistsSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap { (damageClaimId, skip, count) ->
                    val responseObservable = mApiClient.getRespondedSpecialistsPaged(damageClaimId, skip, count)
                            .toObservable()

                    return@flatMap Observables.zip(responseObservable, Observable.just(damageClaimId))
                }
                .publish()
                .autoConnect(2)

        respondedSpecialistObservable
                .observeOn(mSchedulers.provideIo())
                .filter { (response, _) -> response.errorCode == ErrorCode.Remote.REC_DAMAGE_CLAIM_ALREADY_HAS_ASSIGNED_SPECIALIST }
                .map { it.second }
                .subscribe(mDamageClaimAlreadyHasAssignedSpecialistSubject)

        mCompositeDisposable += respondedSpecialistObservable
                .observeOn(mSchedulers.provideIo())
                .filter { (response, _) -> response.errorCode != ErrorCode.Remote.REC_DAMAGE_CLAIM_ALREADY_HAS_ASSIGNED_SPECIALIST }
                .map { it.first }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })

        mDamageClaimAlreadyHasAssignedSpecialistSubject
                .flatMap { mApiClient.getAssignedSpecialist(it).toObservable() }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })

        mCompositeDisposable += mAssignSpecialistSubject
                .subscribeOn(mSchedulers.provideIo())
                .flatMap { packet ->
                    return@flatMap mApiClient.assignSpecialist(packet)
                            .toObservable()
                }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })
    }

    override fun onCleared() {
        Timber.e("RespondedSpecialistsViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    override fun getRespondedSpecialistsSubject(damageClaimId: Long, skip: Long, count: Long) {
        mGetRespondedSpecialistsSubject.onNext(GetRespondedSpecialistsDTO(damageClaimId, skip, count))
    }

    override fun assignSpecialist(userId: Long, damageClaimId: Long) {
        mAssignSpecialistSubject.onNext(AssignSpecialistPacket(userId, damageClaimId))
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is SpecialistsListResponse -> {
                    mOnSpecialistsListResponseSubject.onNext(response.specialists)
                }

                is AssignSpecialistResponse -> {
                    mOnAssignSpecialistResponseSubject.onNext(response)
                }
            }
        } else {
            when (response) {
                is SpecialistsListResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE,
                        ErrorCode.Remote.REC_COULD_NOT_FIND_PROFILE,
                        ErrorCode.Remote.REC_PROFILE_IS_NOT_FILLED_IN -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
                    }
                }

                is AssignSpecialistResponse -> {
                    TODO()
                }
            }
        }
    }

    private fun handleError(error: Throwable) {
        mOnUnknownErrorSubject.onNext(error)
    }

    data class GetRespondedSpecialistsDTO(val damageClaimId: Long,
                                          val skip: Long,
                                          val count: Long)

    override fun onAssignSpecialistResponse(): Observable<AssignSpecialistResponse> = mOnAssignSpecialistResponseSubject
    override fun onSpecialistsListResponse(): Observable<List<SpecialistProfile>> = mOnSpecialistsListResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}