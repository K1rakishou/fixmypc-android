package com.kirakishou.fixmypc.fixmypcapp.util.progress_updater

/**
 * Created by kirakishou on 8/16/2017.
 */

interface FileUploadProgressUpdater {
    fun onPartWrite(percent: Float)
    fun onFileDone()
}