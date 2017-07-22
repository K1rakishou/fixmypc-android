package com.kirakishou.fixmypc.fixmypcapp.api

import android.support.annotation.MainThread
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class RequestFactory
    @Inject constructor(val mApiService: ApiService) {

    @MainThread
    fun LoginRequest(serviceCallbacks: BackgroundServiceCallbacks, testRequestParams: TestRequestParams): Disposable {
        return Single.just("test shit")
                .delay(5, TimeUnit.SECONDS)
                .map { value ->
                    return@map "$value, ${testRequestParams.login}, ${testRequestParams.password}"
                }
                .subscribe({ value ->
                    serviceCallbacks.sendClientAnswer(ServiceAnswer(Constant.EVENT_MESSAGE_TEST, value))
                }, { error ->
                    serviceCallbacks.onUnknownError(error)
                })
    }
}