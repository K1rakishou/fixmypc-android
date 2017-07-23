package com.kirakishou.fixmypc.fixmypcapp.base

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer

/**
 * Created by kirakishou on 7/23/2017.
 */
interface BaseServiceCallbacks : BaseCallbacks {
    fun returnAnswer(answer: ServiceAnswer)
    fun onUnknownError(error: Throwable)
}