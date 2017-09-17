package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ClientNewMalfunctionActivityErrors : ClientNewMalfunctionActivityErrorBase{
    fun onFileSizeExceeded(): Observable<Unit>
    fun onAllFileServersAreNotWorking(): Observable<Unit>
    fun onServerDatabaseError(): Observable<Unit>
    fun onCouldNotConnectToServer(): Observable<Unit>
    fun onPhotosAreNotSelected(): Observable<Unit>
    fun onSelectedPhotoDoesNotExists(): Observable<Unit>
    fun onResponseBodyIsEmpty(): Observable<Unit>
    fun onFileAlreadySelected(): Observable<Unit>
    fun onWifiNotConnected(): Observable<Unit>
    fun onBadOriginalFileNameSubject(): Observable<Unit>
    fun onRequestSizeExceeded(): Observable<Unit>
}