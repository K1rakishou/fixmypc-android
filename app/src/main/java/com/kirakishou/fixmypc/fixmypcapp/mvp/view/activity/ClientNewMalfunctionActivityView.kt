package com.kirakishou.fixmypc.fixmypcapp.mvp.view.activity

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityView

/**
 * Created by kirakishou on 7/31/2017.
 */
interface ClientNewMalfunctionActivityView : BaseActivityView {
    fun onMalfunctionRequestSuccessfullyCreated()
    fun onFileSizeExceeded()
    fun onRequestSizeExceeded()
    fun onAllFileServersAreNotWorking()
    fun onCouldNotConnectToServer(error: Throwable)
    fun onPhotosAreNotSet()
    fun onSelectedPhotoDoesNotExists()
    fun onInitProgressDialog(filesCount: Int)
    fun onProgressDialogUpdate(progress: Int)
    fun onFileUploaded()
    fun onAllFilesUploaded()
    fun onServerDatabaseError()
    fun onFileUploadingError(e: Throwable)
    fun onResponseBodyIsEmpty()
    fun onResetProgressDialog()
    fun onFileAlreadySelected()
}