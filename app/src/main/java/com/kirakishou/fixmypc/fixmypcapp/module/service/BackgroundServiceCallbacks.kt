package com.kirakishou.fixmypc.fixmypcapp.module.service

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks

/**
 * Created by kirakishou on 7/22/2017.
 */
interface BackgroundServiceCallbacks : BaseCallbacks {
    fun onUnknownError(error: Throwable)
}