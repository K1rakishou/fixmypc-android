package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 8/26/2017.
 */
open class ProgressUpdate(val type: ProgressUpdateType)
class ProgressUpdateChunk(val progress: Int) : ProgressUpdate(ProgressUpdateType.Chunk)
class ProgressUpdateFileUploaded : ProgressUpdate(ProgressUpdateType.FileUploaded)
class ProgressUpdateReset : ProgressUpdate(ProgressUpdateType.Reset)

enum class ProgressUpdateType {
    Chunk,
    FileUploaded,
    Reset
}