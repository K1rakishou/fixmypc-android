package com.kirakishou.fixmypc.fixmypcapp.base

/**
 * Created by kirakishou on 8/21/2017.
 */
interface BaseFragmentView {
    fun onShowToast(message: String)
    fun onUnknownError(throwable: Throwable)
}