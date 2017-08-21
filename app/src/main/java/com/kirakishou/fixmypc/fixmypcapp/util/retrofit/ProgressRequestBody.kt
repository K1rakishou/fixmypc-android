package com.kirakishou.fixmypc.fixmypcapp.util.retrofit

import com.kirakishou.fixmypc.fixmypcapp.util.dialog.FileUploadProgressUpdater
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Created by kirakishou on 8/16/2017.
 */
class ProgressRequestBody : RequestBody {

    private val mFile: File
    private val ignoreFirstNumberOfWriteToCalls: Int
    private var lastProgressPercentUpdate = 0f
    private var numWriteToCalls = 0
    private val callback: WeakReference<FileUploadProgressUpdater>

    constructor(file: File, uploadProgressCallback: WeakReference<FileUploadProgressUpdater>) : super() {
        this.mFile = file
        this.callback = uploadProgressCallback
        ignoreFirstNumberOfWriteToCalls = 0
    }

    constructor(file: File, ignoreFirstNumberOfWriteToCalls: Int, uploadProgressCallback: WeakReference<FileUploadProgressUpdater>) : super() {
        this.mFile = file
        this.callback = uploadProgressCallback
        this.ignoreFirstNumberOfWriteToCalls = ignoreFirstNumberOfWriteToCalls
    }

    override fun contentType(): MediaType? {
        return MediaType.parse("multipart/form-data")
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        numWriteToCalls++

        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fis = FileInputStream(mFile)
        var uploaded: Long = 0

        try {
            fis.use {
                var read: Int
                read = it.read(buffer)

                while (read != -1) {
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                    read = it.read(buffer)

                    if (numWriteToCalls > ignoreFirstNumberOfWriteToCalls) {
                        val progress = (uploaded.toFloat() / fileLength.toFloat()) * 100f

                        if (progress - lastProgressPercentUpdate > 5 || progress == 100f) {
                            callback.get()?.onChunkWrite(progress.toInt())
                            lastProgressPercentUpdate = progress
                        }
                    }
                }

                callback.get()?.onFileUploaded()
            }

        } catch (e: Exception) {
            callback.get()?.onError(e)
        }
    }

    companion object {
        private val DEFAULT_BUFFER_SIZE = 2048
    }
}