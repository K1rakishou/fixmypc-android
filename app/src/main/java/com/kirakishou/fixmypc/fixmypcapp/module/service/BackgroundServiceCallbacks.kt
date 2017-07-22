package com.kirakishou.fixmypc.fixmypcapp.module.service

import com.kirakishou.fixmypc.fixmypcapp.base.BasePresenterCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer

/**
 * Created by kirakishou on 7/22/2017.
 */
interface BackgroundServiceCallbacks : BasePresenterCallbacks {
    fun sendClientAnswer(answer: ServiceAnswer)
    fun onUnknownError(error: Throwable)
}