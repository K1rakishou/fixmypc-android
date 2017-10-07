package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.SpecialistsListResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.RespondedSpecialistsActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.RespondedSpecialistsActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.RespondedSpecialistsActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
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

    lateinit var mGetRespondedSpecialistsSubject: BehaviorSubject<GetRespondedSpecialistsDTO>
    lateinit var mOnSpecialistsListResponseSubject: BehaviorSubject<List<SpecialistProfile>>
    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mCompositeDisposable.clear()

        mGetRespondedSpecialistsSubject = BehaviorSubject.create()
        mOnSpecialistsListResponseSubject = BehaviorSubject.create()
        mOnBadResponseSubject = BehaviorSubject.create()
        mOnUnknownErrorSubject = BehaviorSubject.create()

        mCompositeDisposable += mGetRespondedSpecialistsSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { (damageClaimId, skip, count) ->
                    return@flatMap mApiClient.getRespondedSpecialistsPaged(damageClaimId, skip, count)
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

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is SpecialistsListResponse -> {
                    mOnSpecialistsListResponseSubject.onNext(response.specialists)
                }
            }
        } else {
            when (response) {
                is SpecialistsListResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
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

    data class GetRespondedSpecialistsDTO(val damageClaimId: Long,
                                          val skip: Long,
                                          val count: Long)

    override fun mOnSpecialistsListResponse(): Observable<List<SpecialistProfile>> = mOnSpecialistsListResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}