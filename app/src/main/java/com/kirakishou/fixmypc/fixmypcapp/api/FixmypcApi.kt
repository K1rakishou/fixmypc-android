package com.kirakishou.fixmypc.fixmypcapp.api

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import io.reactivex.disposables.Disposable

/**
 * Created by kirakishou on 7/23/2017.
 */
interface FixmypcApi {
    fun LoginRequest(serviceCallbacks: BackgroundServicePresenter, testRequestParams: TestRequestParams): Disposable
}