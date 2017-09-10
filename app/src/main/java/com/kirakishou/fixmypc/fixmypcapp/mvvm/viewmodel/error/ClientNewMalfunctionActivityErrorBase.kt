package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/10/2017.
 */
interface ClientNewMalfunctionActivityErrorBase {
    fun onBadServerResponse(): Observable<Unit>
    fun onUnknownError(): Observable<Throwable>
}