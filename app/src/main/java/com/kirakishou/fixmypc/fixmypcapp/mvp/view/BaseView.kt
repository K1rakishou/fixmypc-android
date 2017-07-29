package com.kirakishou.fixmypc.fixmypcapp.mvp.view

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode

/**
 * Created by kirakishou on 7/20/2017.
 */
interface BaseView : BaseCallbacks {
    fun onShowToast(message: String)
    fun onServerError(serverErrorCode: ServerErrorCode)
    fun onUnknownError(error: Throwable)
}