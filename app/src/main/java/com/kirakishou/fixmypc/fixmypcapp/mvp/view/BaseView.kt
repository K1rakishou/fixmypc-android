package com.kirakishou.fixmypc.fixmypcapp.mvp.view

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode

/**
 * Created by kirakishou on 7/20/2017.
 */
interface BaseView : BaseCallbacks {
    fun onShowToast(message: String)
    fun onServerError(statusCode: StatusCode)
    fun onUnknownError(error: Throwable)
}