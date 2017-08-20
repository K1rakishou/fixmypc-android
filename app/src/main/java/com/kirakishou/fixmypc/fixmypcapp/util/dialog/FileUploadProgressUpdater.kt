package com.kirakishou.fixmypc.fixmypcapp.util.dialog

/**
 * Created by kirakishou on 8/16/2017.
 */

interface FileUploadProgressUpdater {
    fun onPrepareForUploading(filesCount: Int)
    fun onChunkWrite(progress: Int)
    fun onFileDone()
}