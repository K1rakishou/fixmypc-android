package com.kirakishou.fixmypc.fixmypcapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.kirakishou.fixmypc.fixmypcapp.R
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Created by kirakishou on 8/16/2017.
 */
class ProgressDialog(context: Context?) : Dialog(context) {

    @BindView(R.id.current_photo_text)
    lateinit var currentPhotoText: TextView

    @BindView(R.id.progressbar)
    lateinit var progressBar: ProgressBar

    private var unbinder: Unbinder? = null
    private var totalFiles = 0
    private var currentFile = 0
    private val compositeDisposable = CompositeDisposable()
    val progressUpdateSubject = BehaviorSubject.create<ProgressUpdate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_progress)
        unbinder = ButterKnife.bind(this)

        this.currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)

        compositeDisposable += progressUpdateSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ event ->
                    handleEvent(event)
                }, { error ->
                    Timber.e(error)
                })
    }

    override fun onStop() {
        super.onStop()

        unbinder?.unbind()
    }

    private fun handleEvent(event: ProgressUpdate) {
        when (event) {
            is ProgressUpdate.ProgressUpdateStart -> {
                Timber.e("ProgressUpdateStart")
                super.show()

                this.totalFiles = event.filesCount
                this.setCancelable(false)
                this.setTitle(context.getString(com.kirakishou.fixmypc.fixmypcapp.R.string.uploading_in_progress))
            }

            is ProgressUpdate.ProgressUpdateChunk -> {
                Timber.e("ProgressUpdateChunk")
                progressBar.progress = event.progress
            }

            is ProgressUpdate.ProgressUpdateFileUploaded -> {
                Timber.e("ProgressUpdateFileUploaded")
                ++currentFile
                currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)
            }

            is ProgressUpdate.ProgressUpdateReset -> {
                Timber.e("ProgressUpdateReset")
                currentFile = 0
                currentPhotoText.text = String.format(context.getString(R.string.uploading_photo_num), currentFile, totalFiles)
            }

            is ProgressUpdate.ProgressUpdateDone -> {
                Timber.e("ProgressUpdateDone")
                super.hide()
            }
        }
    }
}