package com.kirakishou.fixmypc.fixmypcapp.helper

/**
 * Created by kirakishou on 8/26/2017.
 */
interface ProgressUpdate {
    class ProgressUpdateStart(val filesCount: Int) : ProgressUpdate
    class ProgressUpdateChunk(val progress: Int) : ProgressUpdate
    class ProgressUpdateFileUploaded : ProgressUpdate
    class ProgressUpdateReset : ProgressUpdate
    class ProgressUpdateDone : ProgressUpdate
}