package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiStoreImpl
    @Inject constructor(val mApiService: ApiService,
                        val retrofit: Retrofit) : FixmypcApiStore {

    private val mCompositeDisposable = CompositeDisposable()

    private fun <T> convertErrorBody(errorBody: ResponseBody?, clazz: Class<*>): T {
        val errBody = requireNotNull(errorBody)
        val converter = retrofit.responseBodyConverter<T>(clazz, arrayOf())

        return converter.convert(errBody)
    }

    override fun cleanup() {
        mCompositeDisposable.clear()
    }

    override fun LoginRequest(callbacks: BackgroundServicePresenter, loginRequest: LoginRequest, type: ServiceMessageType) {
        mCompositeDisposable.add(mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .subscribe({ answer ->
                    callbacks.returnAnswer(ServiceAnswer(type, ServerResponse.Success(answer)))
                }, { error ->
                    if (error is HttpException) {
                        val response = convertErrorBody<LoginResponse>(error.response().errorBody(), LoginResponse::class.java)
                        callbacks.returnAnswer(ServiceAnswer(type, ServerResponse.HttpError(StatusCode.from(response.status))))
                    } else {
                        callbacks.returnAnswer(ServiceAnswer(type, ServerResponse.UnknownError(error)))
                    }
                }))
    }
}