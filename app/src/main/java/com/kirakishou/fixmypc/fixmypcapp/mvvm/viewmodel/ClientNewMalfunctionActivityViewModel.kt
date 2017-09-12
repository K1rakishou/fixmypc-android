package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.base.BaseViewModel
import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiClient
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.DamageClaimCategory
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.DamageClaimInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error.ClientNewMalfunctionActivityErrors
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.input.ClientNewMalfunctionActivityInputs
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output.ClientNewMalfunctionActivityOutputs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
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

    private val mUploadProgressUpdateSubject = ReplaySubject.create<ProgressUpdate>()
    private val mSendMalfunctionRequestToServerSubject = BehaviorSubject.create<DamageClaimInfo>()
    private val mOnMalfunctionRequestSuccessfullyCreatedSubject = BehaviorSubject.create<Unit>()
    private val mOnFileSizeExceededSubject = BehaviorSubject.create<Unit>()
    private val mOnRequestSizeExceededSubject = BehaviorSubject.create<Unit>()
    private val mOnAllFileServersAreNotWorkingSubject = BehaviorSubject.create<Unit>()
    private val mOnServerDatabaseErrorSubject = BehaviorSubject.create<Unit>()
    private val mOnCouldNotConnectToServerSubject = BehaviorSubject.create<Unit>()
    private val mOnPhotosAreNotSelectedSubject = BehaviorSubject.create<Unit>()
    private val mOnSelectedPhotoDoesNotExistsSubject = BehaviorSubject.create<Unit>()
    private val mOnResponseBodyIsEmptySubject = BehaviorSubject.create<Unit>()
    private val mOnFileAlreadySelectedSubject = BehaviorSubject.create<Unit>()
    private val mOnBadServerResponseSubject = BehaviorSubject.create<Unit>()
    private val mOnBadOriginalFileNameSubject = BehaviorSubject.create<Unit>()
    private val mOnUnknownErrorSubject = BehaviorSubject.create<Throwable>()

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

    private fun handleResponse(response: DamageClaimResponse) {
        val errorCode = response.errorCode

        if (errorCode != ErrorCode.Remote.REC_OK) {
            handleBadResponse(response.errorCode)
            return
        }

        mOnMalfunctionRequestSuccessfullyCreatedSubject.onNext(Unit)
    }

    private fun handleBadResponse(errorCode: ErrorCode.Remote) {
        when (errorCode) {
            ErrorCode.Remote.REC_NO_PHOTOS_WERE_SELECTED_TO_UPLOAD,
            ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED -> {
                throw IllegalStateException("This should never happen")
            }

            ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> mOnFileSizeExceededSubject.onNext(Unit)
            ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> mOnRequestSizeExceededSubject.onNext(Unit)
            ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> mOnAllFileServersAreNotWorkingSubject.onNext(Unit)
            ErrorCode.Remote.REC_DATABASE_ERROR -> mOnServerDatabaseErrorSubject.onNext(Unit)

            ErrorCode.Remote.REC_TIMEOUT,
            ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER -> {
                mOnCouldNotConnectToServerSubject.onNext(Unit)
            }

            ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS -> mOnSelectedPhotoDoesNotExistsSubject.onNext(Unit)
            ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY -> mOnResponseBodyIsEmptySubject.onNext(Unit)
            ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION -> mOnFileAlreadySelectedSubject.onNext(Unit)
            ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION -> mOnBadServerResponseSubject.onNext(Unit)
            ErrorCode.Remote.REC_BAD_ORIGINAL_FILE_NAME -> TODO()

            else -> throw RuntimeException("Unknown errorCode: $errorCode")
        }
    }

    private fun handleError(error: Throwable) {
        Timber.e(error)
        mOnUnknownErrorSubject.onNext(error)
    }

    override fun uploadProgressUpdateSubject(): Observable<ProgressUpdate> = mUploadProgressUpdateSubject
    override fun onMalfunctionRequestSuccessfullyCreated(): Observable<Unit> = mOnMalfunctionRequestSuccessfullyCreatedSubject
    override fun onFileSizeExceeded(): Observable<Unit> = mOnFileSizeExceededSubject
    override fun onAllFileServersAreNotWorking(): Observable<Unit> = mOnAllFileServersAreNotWorkingSubject
    override fun onServerDatabaseError(): Observable<Unit> = mOnServerDatabaseErrorSubject
    override fun onCouldNotConnectToServer(): Observable<Unit> = mOnCouldNotConnectToServerSubject
    override fun onPhotosAreNotSelected(): Observable<Unit> = mOnPhotosAreNotSelectedSubject
    override fun onSelectedPhotoDoesNotExists(): Observable<Unit> = mOnSelectedPhotoDoesNotExistsSubject
    override fun onResponseBodyIsEmpty(): Observable<Unit> = mOnResponseBodyIsEmptySubject
    override fun onFileAlreadySelected(): Observable<Unit> = mOnFileAlreadySelectedSubject
    override fun onBadServerResponse(): Observable<Unit> = mOnBadServerResponseSubject
    override fun onBadOriginalFileNameSubject(): Observable<Unit> = mOnBadOriginalFileNameSubject
    override fun onUnknownError(): Observable<Throwable> = mOnUnknownErrorSubject
}