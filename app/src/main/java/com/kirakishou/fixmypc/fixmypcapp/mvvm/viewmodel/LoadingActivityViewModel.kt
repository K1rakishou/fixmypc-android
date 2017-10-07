package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.scheduler.SchedulerProvider
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginResponseDataDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.LoginPacket
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.LoadingActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.LoadingActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.LoadingActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
class LoadingActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings,
                    protected val mSchedulers: SchedulerProvider) :
        BaseViewModel(),
        LoadingActivityInputs,
        LoadingActivityOutputs,
        LoadingActivityErrors {

    val mInputs: LoadingActivityInputs = this
    val mOutputs: LoadingActivityOutputs = this
    val mErrors: LoadingActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    lateinit var mLogInSubject: BehaviorSubject<LoginPasswordDTO>
    lateinit var mRunClientActivitySubject: BehaviorSubject<LoginResponseDataDTO>
    lateinit var mRunSpecialistMainActivitySubject: BehaviorSubject<LoginResponseDataDTO>
    lateinit var mUnknownErrorSubject: BehaviorSubject<Throwable>
    lateinit var mOnBadResponseSubject: BehaviorSubject<ErrorCode.Remote>

    fun init() {
        mCompositeDisposable.clear()

        mLogInSubject = BehaviorSubject.create<LoginPasswordDTO>()
        mRunClientActivitySubject = BehaviorSubject.create<LoginResponseDataDTO>()
        mRunSpecialistMainActivitySubject = BehaviorSubject.create<LoginResponseDataDTO>()
        mUnknownErrorSubject = BehaviorSubject.create<Throwable>()
        mOnBadResponseSubject = BehaviorSubject.create<ErrorCode.Remote>()

        mCompositeDisposable += mLogInSubject
                .subscribeOn(mSchedulers.provideIo())
                .map { LoginPacket(it.login, it.password) }
                .doOnNext { mAppSettings.saveUserInfo(it.login, it.password, "") }
                .flatMap { mApiClient.loginRequest(it).toObservable() }
                .subscribe({
                    Timber.e("Login response received")
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    override fun onCleared() {
        Timber.e("LoadingActivityViewModel.onCleared()")
        mCompositeDisposable.clear()

        super.onCleared()
    }

    override fun startLoggingIn(params: LoginPasswordDTO) {
        mLogInSubject.onNext(params)
    }

    private fun handleResponse(response: LoginResponse) {
        val sessionId = response.sessionId
        val accountType = response.accountType
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            handleBadResponse(response.errorCode)
            return
        }

        mAppSettings.updateSessionId(sessionId)

        when (accountType) {
            AccountType.Client -> {
                val userInfo = mAppSettings.loadUserInfo()
                mRunClientActivitySubject.onNext(LoginResponseDataDTO(userInfo.login, userInfo.password, sessionId, accountType))
            }

            AccountType.Specialist -> {
                val userInfo = mAppSettings.loadUserInfo()
                mRunSpecialistMainActivitySubject.onNext(LoginResponseDataDTO(userInfo.login, userInfo.password, sessionId, accountType))
            }

            //should never happen
            else -> throw IllegalStateException("Server returned unknown accountType $accountType")
        }
    }

    private fun handleBadResponse(errorCode: ErrorCode.Remote) {
        when (errorCode) {
            ErrorCode.Remote.REC_TIMEOUT,
            ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER,
            ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION,
            ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD,
            ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR,
            ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> {
                mOnBadResponseSubject.onNext(errorCode)
            }

            else -> throw RuntimeException("Unknown errorCode: $errorCode")
        }
    }

    private fun handleError(error: Throwable) {
        mUnknownErrorSubject.onNext(error)
    }

    override fun runClientMainActivity(): Observable<LoginResponseDataDTO> = mRunClientActivitySubject
    override fun runSpecialistMainActivity(): Observable<LoginResponseDataDTO> = mRunSpecialistMainActivitySubject
    override fun onUnknownError(): Observable<Throwable> = mUnknownErrorSubject
    override fun onBadResponse(): Observable<ErrorCode.Remote> = mOnBadResponseSubject
}