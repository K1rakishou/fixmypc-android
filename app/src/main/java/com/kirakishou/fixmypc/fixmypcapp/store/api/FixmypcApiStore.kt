package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter

/**
 * Created by kirakishou on 7/23/2017.
 */
interface FixmypcApiStore {
    var callbacks: Fickle<BackgroundServicePresenter>

    fun cleanup()
    fun loginRequest(loginRequest: LoginRequest, type: ServiceMessageType)
}