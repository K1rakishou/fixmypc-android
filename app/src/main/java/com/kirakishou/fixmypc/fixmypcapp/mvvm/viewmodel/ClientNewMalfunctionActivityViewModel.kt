package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.MalfunctionResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.ApiException
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ClientNewMalfunctionActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ClientNewMalfunctionActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ClientNewMalfunctionActivityOutputs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
class ClientNewMalfunctionActivityViewModel
@Inject constructor(protected val mApiClient: ApiClient) : BaseViewModel(),
        ClientNewMalfunctionActivityInputs, ClientNewMalfunctionActivityOutputs,
        ClientNewMalfunctionActivityErrors {

    val mInputs: ClientNewMalfunctionActivityInputs = this
    val mOutputs: ClientNewMalfunctionActivityOutputs = this
    val mErrors: ClientNewMalfunctionActivityErrors = this

    private val mCompositeDisposable = CompositeDisposable()
    private val malfunctionRequestInfo = DamageClaimInfo()

    lateinit var mUploadProgressUpdateSubject: BehaviorSubject<ProgressUpdate>
    private val mSendMalfunctionRequestToServerSubject = BehaviorSubject.create<DamageClaimInfo>()

    init {
        mCompositeDisposable += mSendMalfunctionRequestToServerSubject
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { mUploadProgressUpdateSubject.onNext(ProgressUpdate.ProgressUpdateStart(it.damageClaimPhotos.size)) }
                .observeOn(Schedulers.io())
                .flatMap { mApiClient.createMalfunctionRequest(it, mUploadProgressUpdateSubject).toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { mUploadProgressUpdateSubject.onNext(ProgressUpdate.ProgressUpdateDone()) }
                .subscribe({
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    override fun onCleared() {
        super.onCleared()

        Timber.e("ClientNewMalfunctionActivityViewModel.onCleared()")
        mCompositeDisposable.clear()
    }

    fun setCategory(category: DamageClaimCategory) {
        malfunctionRequestInfo.damageClaimCategory = category
    }

    fun setDescription(description: String) {
        malfunctionRequestInfo.damageClaimDescription = description
    }

    fun setPhotos(photos: List<String>) {
        malfunctionRequestInfo.damageClaimPhotos = ArrayList(photos)
    }

    fun setLocation(location: LatLng) {
        malfunctionRequestInfo.damageClaimLocation = location
    }

    override fun sendMalfunctionRequestToServer() {
        mSendMalfunctionRequestToServerSubject.onNext(malfunctionRequestInfo)
    }

    private fun handleResponse(response: MalfunctionResponse) {
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            throw IllegalStateException("ServerResponse is Success but errorCode is not SEC_OK: $errorCode")
        }

        /*callbacks.onAllFilesUploaded()
        callbacks.onMalfunctionRequestSuccessfullyCreated()*/
    }

    private fun handleError(error: Throwable) {
        if (error !is ApiException) {
            Timber.e(error)
        }

        //callbacks.onAllFilesUploaded()

        when (error) {
            /*is ApiException -> {
                val remoteErrorCode = error.errorCode

                when (remoteErrorCode) {
                    //Client should check for these two. They should never happen unless the client is patched
                    ErrorCode.Remote.REC_NO_FILES_WERE_SELECTED_TO_UPLOAD,
                    ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED -> {
                        throw IllegalStateException("This should never happen")
                    }

                    ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> callbacks.onFileSizeExceeded()
                    ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> callbacks.onRequestSizeExceeded()
                    ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> callbacks.onAllFileServersAreNotWorking()
                    ErrorCode.Remote.REC_DATABASE_ERROR -> callbacks.onServerDatabaseError()

                    else -> throw IllegalStateException("Unknown error code remoteErrorCode = $remoteErrorCode")
                }
            }

            is TimeoutException,
            is UnknownHostException -> {
                callbacks.onCouldNotConnectToServer(error)
            }

            is FileSizeExceededException -> callbacks.onFileSizeExceeded()
            is PhotosAreNotSetException -> callbacks.onPhotosAreNotSet()
            is SelectedPhotoDoesNotExistsException -> callbacks.onSelectedPhotoDoesNotExists()
            is ResponseBodyIsEmpty -> callbacks.onResponseBodyIsEmpty()
            is DuplicateObservableException -> callbacks.onFileAlreadySelected()

            else -> callbacks.onUnknownError(error)*/
        }
    }
}