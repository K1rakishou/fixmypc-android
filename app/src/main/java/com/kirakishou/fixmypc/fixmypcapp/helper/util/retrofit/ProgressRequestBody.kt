package com.kirakishou.fixmypc.fixmypcapp.helper.util.retrofit

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdateChunk
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdateFileUploaded
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by kirakishou on 8/16/2017.
 */
class ProgressRequestBody : RequestBody {

    private val mFile: File
    private val ignoreFirstNumberOfWriteToCalls: Int
    private var lastProgressPercentUpdate = 0f
    private var numWriteToCalls = 0
    private lateinit var uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>

    constructor(file: File, uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>) : super() {
        this.mFile = file
        this.uploadProgressUpdateSubject = uploadProgressUpdateSubject
        ignoreFirstNumberOfWriteToCalls = 0
    }

    constructor(file: File, ignoreFirstNumberOfWriteToCalls: Int, uploadProgressUpdateSubject: PublishSubject<ProgressUpdate>) : super() {
        this.mFile = file
        this.uploadProgressUpdateSubject = uploadProgressUpdateSubject
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
                            //callback.get()?.onChunkWrite(progress.toInt())
                            uploadProgressUpdateSubject.onNext(ProgressUpdateChunk(progress.toInt()))
                            lastProgressPercentUpdate = progress
                        }
                    }
                }

                //callback.get()?.onFileUploaded()
                uploadProgressUpdateSubject.onNext(ProgressUpdateFileUploaded())
            }

        } catch (e: Exception) {
            uploadProgressUpdateSubject.onError(e)
        }
    }

    companion object {
        private val DEFAULT_BUFFER_SIZE = 2048
    }
}