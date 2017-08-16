package com.kirakishou.fixmypc.fixmypcapp.util.retrofit

import io.reactivex.Observable
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

    val mFile: File
    val ignoreFirstNumberOfWriteToCalls: Int
    var lastProgressPercentUpdate = 0f
    var numWriteToCalls = 0
    protected val getProgressSubject: PublishSubject<Float> = PublishSubject.create<Float>()

    constructor(file: File) : super() {
        this.mFile = file
        ignoreFirstNumberOfWriteToCalls = 0
    }

    constructor(file: File, ignoreFirstNumberOfWriteToCalls: Int) : super() {
        this.mFile = file
        this.ignoreFirstNumberOfWriteToCalls = ignoreFirstNumberOfWriteToCalls
    }

    fun getProgressSubject(): Observable<Float> {
        return getProgressSubject
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

                        if (progress - lastProgressPercentUpdate > 3 || progress == 100f) {
                            getProgressSubject.onNext(progress)
                            lastProgressPercentUpdate = progress
                        }
                    }
                }

                getProgressSubject.onComplete()
            }

        } catch (e: Exception) {
            getProgressSubject.onError(e)
        }
    }

    companion object {
        private val DEFAULT_BUFFER_SIZE = 2048
    }
}