package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ClientMainActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ClientMainActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ClientMainActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 8/21/2017.
 */
class ClientMainActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings,
                    protected val mSchedulers: SchedulerProvider) : BaseViewModel(),
        ClientMainActivityInputs,
        ClientMainActivityOutputs,
        ClientMainActivityErrors {

    val mInputs: ClientMainActivityInputs = this
    val mOutputs: ClientMainActivityOutputs = this
    val mErrors: ClientMainActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    lateinit var mGetActiveClientDamageClaimSubject: BehaviorSubject<GetClientDamageClaimsDTO>
    lateinit var mGetInactiveClientDamageClaimSubject: BehaviorSubject<GetClientDamageClaimsDTO>
    lateinit var mOnActiveDamageClaimsResponseSubject: BehaviorSubject<MutableList<DamageClaim>>
    lateinit var mOnInactiveDamageClaimsResponseSubject: BehaviorSubject<MutableList<DamageClaim>>
    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>
    lateinit var mOnUnknownErrorSubject: BehaviorSubject<Throwable>

    fun init() {
        mGetActiveClientDamageClaimSubject = BehaviorSubject.create()
        mGetInactiveClientDamageClaimSubject = BehaviorSubject.create()

        mCompositeDisposable += mGetActiveClientDamageClaimSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { (isActive, skip, count) ->
                    return@flatMap mApiClient.getClientDamageClaimsPaged(isActive, skip, count)
                            .toObservable()
                }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })

        mCompositeDisposable += mGetInactiveClientDamageClaimSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { (isActive, skip, count) ->
                    return@flatMap mApiClient.getClientDamageClaimsPaged(isActive, skip, count)
                            .toObservable()
                }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })
    }

    override fun getActiveClientDamageClaimSubject( skip: Long, count: Long) {
        mGetActiveClientDamageClaimSubject.onNext(GetClientDamageClaimsDTO(true, skip, count))
    }

    override fun getInactiveClientDamageClaimSubject(skip: Long, count: Long) {
        mGetInactiveClientDamageClaimSubject.onNext(GetClientDamageClaimsDTO(false, skip, count))
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is DamageClaimsResponse -> {
                    if (response.damageClaims.first().isActive) {
                        mOnActiveDamageClaimsResponseSubject.onNext(response.damageClaims)
                    } else {
                        mOnInactiveDamageClaimsResponseSubject.onNext(response.damageClaims)
                    }
                }
            }
        } else {
            when (response) {
                is DamageClaimsResponse -> {
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

    override fun onCleared() {
        super.onCleared()

        Timber.e("ClientMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    override fun onActiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>> = mOnActiveDamageClaimsResponseSubject
    override fun onInactiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>> = mOnInactiveDamageClaimsResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject

    data class GetClientDamageClaimsDTO(val isActive: Boolean,
                                        val skip: Long,
                                        val count: Long)
}