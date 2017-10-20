package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.ClientProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaim
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.*
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.UnknownErrorCodeException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error.ClientMainActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input.ClientMainActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output.ClientMainActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
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

    private val itemsPerPage = Constant.MAX_DAMAGE_CLAIMS_PER_PAGE
    private val mCompositeDisposable = CompositeDisposable()

    private val mOnGetClientProfileResponseSubject = PublishSubject.create<ClientProfile>()
    private val mGetClientProfileSubject = PublishSubject.create<Unit>()
    private val mGetActiveClientDamageClaimSubject = PublishSubject.create<GetClientDamageClaimsDTO>()
    private val mGetInactiveClientDamageClaimSubject = PublishSubject.create<GetClientDamageClaimsDTO>()
    private val mOnActiveDamageClaimsResponseSubject = PublishSubject.create<DamageClaimsWithCountResponse>()
    private val mOnInactiveDamageClaimsResponseSubject = PublishSubject.create<MutableList<DamageClaim>>()
    private val mOnBadResponseSubject = PublishSubject.create<ErrorCode.Remote>()
    private val mOnUnknownErrorSubject = PublishSubject.create<Throwable>()

    init {
        mCompositeDisposable += mGetActiveClientDamageClaimSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { (isActive, skip, count) -> mApiClient.getClientDamageClaimsPaged(isActive, skip, count)
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
                .flatMap { (isActive, skip, count) -> mApiClient.getClientDamageClaimsPaged(isActive, skip, count)
                            .toObservable()
                }
                .subscribe({
                    handleResponse(DamageClaimsClientResponse(it.damageClaims, it.errorCode))
                }, { error ->
                    handleError(error)
                })

        mCompositeDisposable += mGetClientProfileSubject
                .subscribeOn(mSchedulers.provideIo())
                .observeOn(mSchedulers.provideIo())
                .flatMap { mApiClient.getClientProfile().toObservable() }
                .subscribe({
                    handleResponse(it)
                }, { error ->
                    handleError(error)
                })
    }

    override fun onCleared() {
        Timber.e("ClientMainActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    override fun getActiveClientDamageClaimSubject( skip: Long, count: Long) {
        mGetActiveClientDamageClaimSubject.onNext(GetClientDamageClaimsDTO(true, skip * itemsPerPage, count))
    }

    override fun getInactiveClientDamageClaimSubject(skip: Long, count: Long) {
        mGetInactiveClientDamageClaimSubject.onNext(GetClientDamageClaimsDTO(false, skip * itemsPerPage, count))
    }

    override fun getClientProfile() {
        mGetClientProfileSubject.onNext(Unit)
    }

    private fun handleResponse(response: StatusResponse) {
        val errorCode = response.errorCode

        if (errorCode == ErrorCode.Remote.REC_OK) {
            when (response) {
                is DamageClaimsWithCountResponse -> {
                    mOnActiveDamageClaimsResponseSubject.onNext(response)
                }

                is DamageClaimsClientResponse -> {
                    mOnInactiveDamageClaimsResponseSubject.onNext(response.damageClaims)
                }

                is ClientProfileResponse -> {
                    mOnGetClientProfileResponseSubject.onNext(response.clientProfile)
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

                        else -> mOnUnknownErrorSubject.onNext(UnknownErrorCodeException("Unknown errorCode: $errorCode"))
                    }
                }

                is ClientProfileResponse -> {
                    TODO()
                }
            }
        }
    }

    private fun handleError(error: Throwable) {
        mOnUnknownErrorSubject.onNext(error)
    }

    override fun onGetClientProfileResponse(): Observable<ClientProfile> = mOnGetClientProfileResponseSubject
    override fun onActiveDamageClaimsResponse(): Observable<DamageClaimsWithCountResponse> = mOnActiveDamageClaimsResponseSubject
    override fun onInactiveDamageClaimsResponse(): Observable<MutableList<DamageClaim>> = mOnInactiveDamageClaimsResponseSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject

    data class GetClientDamageClaimsDTO(val isActive: Boolean,
                                        val skip: Long,
                                        val count: Long)
}