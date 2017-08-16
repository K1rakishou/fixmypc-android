package com.kirakishou.fixmypc.fixmypcapp.mvp.view

/**
 * Created by kirakishou on 7/31/2017.
 */
interface ClientMainActivityView : BaseView {
    fun onMalfunctionRequestSuccessfullyCreated()
    fun onFileSizeExceeded()
    fun onRequestSizeExceeded()
    fun onAllFileServersAreNotWorking()
    fun onCouldNotConnectToServer(error: Throwable)
}