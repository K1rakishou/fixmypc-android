package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity

import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AppSettings
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity.LoadingActivityView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
open class LoadingActivityPresenterImpl
@Inject constructor(protected val mApiClient: ApiClient,
                    protected val mAppSettings: AppSettings) : LoadingActivityPresenter<LoadingActivityView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("LoadingActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("LoadingActivityPresenterImpl.destroyPresenter()")
    }

    override fun startLoggingIn(login: String, password: String) {
        mApiClient.loginRequest(LoginRequest(login, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ handleResponse(login, password, it) }, { handleError(it) })
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
                callbacks.runClientMainActivity(sessionId, accountType)
            }

            AccountType.Specialist -> {
                callbacks.runSpecialistMainActivity(sessionId, accountType)
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
                        callbacks.runGuestMainActivity()
                    }

                    else -> throw IllegalStateException("This should never happen errCode = $remoteErrorCode")
                }
            }

            is TimeoutException,
            is UnknownHostException -> {
                callbacks.onCouldNotConnectToServer(error)
            }

            else -> callbacks.onUnknownError(error)
        }
    }
}