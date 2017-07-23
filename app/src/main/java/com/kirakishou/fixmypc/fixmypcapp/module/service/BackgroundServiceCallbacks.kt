package com.kirakishou.fixmypc.fixmypcapp.module.service

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer

/**
 * Created by kirakishou on 7/22/2017.
 */
interface BackgroundServiceCallbacks : BaseCallbacks {
    fun onUnknownError(error: Throwable)
    fun sendClientAnswer(answer: ServiceAnswer)
}