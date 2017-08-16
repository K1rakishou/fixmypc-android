package com.kirakishou.fixmypc.fixmypcapp.util.dialog

/**
 * Created by kirakishou on 8/16/2017.
 */

interface FileUploadProgressUpdater {
    fun init(filesCount: Int)
    fun onPartWrite(progress: Int)
    fun onFileDone()
}