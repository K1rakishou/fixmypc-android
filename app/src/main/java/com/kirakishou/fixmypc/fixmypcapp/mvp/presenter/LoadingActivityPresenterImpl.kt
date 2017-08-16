package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import com.kirakishou.fixmypc.fixmypcapp.store.api.FixmypcApiStore
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/20/2017.
 */
open class LoadingActivityPresenterImpl
@Inject constructor(protected val mFixmypcApiStore: FixmypcApiStore,
                    protected val errorBodyConverter: ErrorBodyConverter) : LoadingActivityPresenter<LoadingActivityView>() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun initPresenter() {
        Timber.d("LoadingActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        mCompositeDisposable.clear()

        Timber.d("LoadingActivityPresenterImpl.destroyPresenter()")
    }

    override fun startLoggingIn(login: String, password: String) {
        mCompositeDisposable += mFixmypcApiStore.loginRequest(LoginRequest(login, password))
                .subscribe({ response ->
                    val sessionId = response.sessionId
                    val accountType = response.accountType
                    val errorCode = response.errorCode

                    if (errorCode != ErrorCode.Remote.REC_OK) {
                        throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
                    }

                    when (accountType) {
                        AccountType.Client -> {
                            callbacks.runClientMainActivity(sessionId, accountType)
                        }

                        AccountType.Specialist -> {
                            callbacks.runSpecialistMainActivity(sessionId, accountType)
                        }

                        //should never happen
                        else -> throw IllegalStateException("Server returned accountType ${accountType}")
                    }
                }, { error ->
                    handleResponse(error)
                })
    }

    private fun handleResponse(error: Throwable) {
        Timber.e(error)

        when (error) {
            is HttpException -> {
                val response = errorBodyConverter.convert<LoginResponse>(error.response().errorBody()!!.string(), LoginResponse::class.java)
                val remoteErrorCode = response.errorCode

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