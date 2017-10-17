package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.RespondToDamageClaimPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.HasAlreadyRespondedResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.RespondToDamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.SpecialistProfileResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.DamageClaimFullInfoActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.SpecialistMainActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.DamageClaimFullInfoActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.SpecialistMainActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.DamageClaimFullInfoActivityOutputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.SpecialistMainActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 10/15/2017.
 */
class DamageClaimFullInfoActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        DamageClaimFullInfoActivityInputs,
        DamageClaimFullInfoActivityOutputs,
        DamageClaimFullInfoActivityErrors {

    val mInputs: DamageClaimFullInfoActivityInputs = this
    val mOutputs: DamageClaimFullInfoActivityOutputs = this
    val mErrors: DamageClaimFullInfoActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    lateinit var mCheckHasAlreadyRespondedSubject: BehaviorSubject<Long>
    lateinit var mRespondToDamageClaimSubject: BehaviorSubject<Long>
    lateinit var mOnHasAlreadyRespondedResponse: BehaviorSubject<Boolean>
    lateinit var mOnRespondToDamageClaimSuccessSubject: BehaviorSubject<Unit>
    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mCheckHasAlreadyRespondedSubject = BehaviorSubject.create()
        mRespondToDamageClaimSubject = BehaviorSubject.create()
        mOnHasAlreadyRespondedResponse = BehaviorSubject.create()
        mOnRespondToDamageClaimSuccessSubject = BehaviorSubject.create()
        mOnBadResponseSubject = BehaviorSubject.create()
        mOnUnknownErrorSubject = BehaviorSubject.create()

        mCompositeDisposable += mCheckHasAlreadyRespondedSubject
                .flatMap { mApiClient.checkAlreadyRespondedToDamageClaim(it).toObservable() }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })

        mCompositeDisposable += mRespondToDamageClaimSubject
                .flatMap { mApiClient.respondToDamageClaim(RespondToDamageClaimPacket(it)).toObservable() }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    override fun onCleared() {
        Timber.e("DamageClaimFullInfoActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    override fun checkHasAlreadyRespondedToDamageClaim(damageClaimId: Long) {
        mCheckHasAlreadyRespondedSubject.onNext(damageClaimId)
    }

    override fun respondToDamageClaim(damageClaimId: Long) {
        mRespondToDamageClaimSubject.onNext(damageClaimId)
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is RespondToDamageClaimResponse -> {
                    mOnRespondToDamageClaimSuccessSubject.onNext(Unit)
                }

                is HasAlreadyRespondedResponse -> {
                    mOnHasAlreadyRespondedResponse.onNext(response.hasAlreadyResponded)
                }
            }
        } else {
            when (response) {
                is RespondToDamageClaimResponse -> {
                    when (errorCode) {
                        ErrorCode.Remote.REC_TIMEOUT,
                        ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
                        ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
                        ErrorCode.Remote.REC_COULD_NOT_RESPOND_TO_DAMAGE_CLAIM,
                        ErrorCode.Remote.REC_DAMAGE_CLAIM_DOES_NOT_EXIST,
                        ErrorCode.Remote.REC_DAMAGE_CLAIM_IS_NOT_ACTIVE,
                        ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE,
                        ErrorCode.Remote.REC_COULD_NOT_FIND_PROFILE,
                        ErrorCode.Remote.REC_PROFILE_IS_NOT_FILLED_IN -> {
                            mOnBadResponseSubject.onNext(errorCode)
                        }

                        else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
                    }
                }

                is HasAlreadyRespondedResponse -> {
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

    override fun onRespondToDamageClaimSuccessSubject(): Observable<Unit> = mOnRespondToDamageClaimSuccessSubject
    override fun onHasAlreadyRespondedResponse(): Observable<Boolean> = mOnHasAlreadyRespondedResponse
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}