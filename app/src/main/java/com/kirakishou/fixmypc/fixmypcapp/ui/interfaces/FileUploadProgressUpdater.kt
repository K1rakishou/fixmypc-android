package com.kirakishou.fixmypc.fixmypcapp.ui.interfaces

/**
 * Created by kirakishou on 8/16/2017.
 */

interface FileUploadProgressUpdater {
    fun onPrepareForUploading(filesCount: Int)
    fun onChunkWrite(progress: Int)
    fun onFileUploaded()
    fun onError(e: Throwable)
    fun onReset()
}