package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request_params.TestRequestParams

/**
 * Created by kirakishou on 7/22/2017.
 */
interface BackgroundServicePresenter : BaseCallbacks {
    fun testRequest(testRequestParams: TestRequestParams)
}