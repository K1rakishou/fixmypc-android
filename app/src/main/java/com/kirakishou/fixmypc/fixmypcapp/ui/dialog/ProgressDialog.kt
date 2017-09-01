package com.kirakishou.fixmypc.fixmypcapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.R

/**
 * Created by kirakishou on 8/16/2017.
 */
class ProgressDialog : Dialog {

    @BindView(R.id.current_photo_text)
    lateinit var currentPhotoText: TextView

    @BindView(R.id.progressbar)
    lateinit var progressBar: ProgressBar

    private var unbinder: Unbinder? = null
    private var totalFiles = 0
    private var currentFile = 0
    private lateinit var handler: Handler

    private constructor() : super(null)

    constructor(ctx: Context) : super(ctx) {
        handler = Handler(ctx.mainLooper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        unbinder = ButterKnife.bind(this)

        this.currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)
    }

    override fun onStop() {
        super.onStop()

        unbinder?.unbind()
    }

    fun init(filesCount: Int) {
        handler.post {
            this.totalFiles = filesCount
            this.setCancelable(false)
            this.setTitle(context.getString(com.kirakishou.fixmypc.fixmypcapp.R.string.uploading_in_progress))
        }
    }

    override fun show() {
        handler.post {
            super.show()
        }
    }

    override fun hide() {
        handler.post {
            super.hide()
        }
    }

    fun setProgress(progress: Int) {
        handler.post {
            this.progressBar.progress = progress
        }
    }

    fun onFileUploaded() {
        handler.post {
            ++currentFile
            this.currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)
        }
    }

    fun reset() {
        handler.post {
            currentFile = 0
            this.currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)
        }
    }
}