package com.kirakishou.fixmypc.fixmypcapp.api

import com.kirakishou.fixmypc.fixmypcapp.api.retrofit.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiImpl
    @Inject constructor(val mApiService: ApiService) : FixmypcApi {

    override fun LoginRequest(serviceCallbacks: BackgroundServicePresenter, testRequestParams: TestRequestParams): Disposable {
        return Single.just("test shit")
                .delay(5, TimeUnit.SECONDS)
                .map { value ->
                    return@map "$value, ${testRequestParams.login}, ${testRequestParams.password}"
                }
                .subscribe({ value ->
                    serviceCallbacks.returnAnswer(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN, value))
                }, { error ->
                    serviceCallbacks.onUnknownError(error)
                })
    }
}