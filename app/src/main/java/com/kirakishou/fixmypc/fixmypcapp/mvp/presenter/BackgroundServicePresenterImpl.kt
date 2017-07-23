package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.api.RequestFactory
import com.kirakishou.fixmypc.fixmypcapp.base.BasePresenter
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class BackgroundServicePresenterImpl
    @Inject constructor(val requestFactory: RequestFactory) : BasePresenter<BackgroundServiceCallbacks>(), BackgroundServicePresenter {

    override fun testRequest(testRequestParams: TestRequestParams) {
        requestFactory.LoginRequest(callbacks, testRequestParams)
    }
}