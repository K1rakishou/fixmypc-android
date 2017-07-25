package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseServiceCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.request.LoginRequest

/**
 * Created by kirakishou on 7/22/2017.
 */
interface BackgroundServicePresenter : BaseServiceCallbacks {
    fun testRequest(loginRequest: LoginRequest)
}