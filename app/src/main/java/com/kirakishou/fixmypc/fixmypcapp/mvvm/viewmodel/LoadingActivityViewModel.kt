package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginResponseDataDTO
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.LoadingActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.LoadingActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.LoadingActivityOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
class LoadingActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings) : BaseViewModel(),
        LoadingActivityInputs,
        LoadingActivityOutputs,
        LoadingActivityErrors {

    val mInputs: LoadingActivityInputs = this
    val mOutputs: LoadingActivityOutputs = this
    val mErrors: LoadingActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()

    private val mLogInSubject = BehaviorSubject.create<LoginPasswordDTO>()
    private val mRunClientActivitySubject = BehaviorSubject.create<LoginResponseDataDTO>()
    private val mRunSpecialistMainActivitySubject = BehaviorSubject.create<LoginResponseDataDTO>()
    private val mRunGuestMainActivity = BehaviorSubject.create<Boolean>()
    private val mCouldNotConnectToServerSubject = BehaviorSubject.create<Throwable>()
    private val mUnknownErrorSubject = BehaviorSubject.create<Throwable>()

    init {
        mCompositeDisposable += mLogInSubject
                .subscribeOn(Schedulers.io())
                .map { LoginRequest(it.login, it.password) }
                .flatMap { Observables.zip(mApiClient.loginRequest(it).toObservable(), Observable.just(it)) }
                .subscribe({ (loginResponse, loginRequest) ->
                    Timber.e("Login response received")
                    handleResponse(loginRequest.login, loginRequest.password, loginResponse)
                }, {
                    handleError(it)
                })
    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("LoadingActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    override fun startLoggingIn(params: LoginPasswordDTO) {
        mLogInSubject.onNext(params)
    }

    private fun handleResponse(login: String, password: String, response: LoginResponse) {
        val sessionId = response.sessionId
        val accountType = response.accountType
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
        }

        mAppSettings.saveUserInfo(login, password, sessionId)

        when (accountType) {
            AccountType.Client -> {
                mRunClientActivitySubject.onNext(LoginResponseDataDTO(sessionId, accountType))
            }

            AccountType.Specialist -> {
                mRunSpecialistMainActivitySubject.onNext(LoginResponseDataDTO(sessionId, accountType))
            }

            //should never happen
            else -> throw IllegalStateException("Server returned unknown accountType $accountType")
        }
    }

    private fun handleError(error: Throwable) {
        if (error !is ApiException) {
            Timber.e(error)
        }

        when (error) {
            is ApiException -> {
                val remoteErrorCode = error.errorCode

                when (remoteErrorCode) {
                    ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD,
                    ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR -> {
                        mRunGuestMainActivity.onNext(true)
                    }

                    else -> throw IllegalStateException("This should never happen errCode = $remoteErrorCode")
                }
            }

            is TimeoutException,
            is UnknownHostException -> {
                mCouldNotConnectToServerSubject.onNext(error)
            }

            else -> mUnknownErrorSubject.onNext(error)
        }
    }

    override fun runClientMainActivity(): Observable<LoginResponseDataDTO> = mRunClientActivitySubject
    override fun runSpecialistMainActivity(): Observable<LoginResponseDataDTO> = mRunSpecialistMainActivitySubject
    override fun runGuestMainActivity(): Observable<Boolean> = mRunGuestMainActivity
    override fun onCouldNotConnectToServer(): Observable<Throwable> = mCouldNotConnectToServerSubject
    override fun onUnknownError(): Observable<Throwable> = mUnknownErrorSubject
}