package com.kirakishou.fixmypc.fixmypcapp.mvp.view

import com.kirakishou.fixmypc.fixmypcapp.base.BasePresenterCallbacks

/**
 * Created by kirakishou on 7/20/2017.
 */
interface BaseView : BasePresenterCallbacks {
    fun onShowToast(message: String)
    fun onUnknownError(error: Throwable)
}