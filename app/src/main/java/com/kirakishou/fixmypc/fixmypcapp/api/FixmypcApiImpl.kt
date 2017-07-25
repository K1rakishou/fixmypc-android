package com.kirakishou.fixmypc.fixmypcapp.api

import com.kirakishou.fixmypc.fixmypcapp.api.retrofit.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType.SERVICE_MESSAGE_LOGIN
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiImpl
    @Inject constructor(val mApiService: ApiService) : FixmypcApi {

    private val mCompositeDisposable = CompositeDisposable()

    override fun cleanup() {
        mCompositeDisposable.clear()
    }

    override fun LoginRequest(callbacks: BackgroundServicePresenter, loginRequest: LoginRequest) {
        mCompositeDisposable.add(mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .subscribe({ answer ->
                    callbacks.returnAnswer(ServiceAnswer(SERVICE_MESSAGE_LOGIN, Fickle.of(answer)))
                }, { error ->
                    callbacks.onUnknownError(error)
                }))
    }
}