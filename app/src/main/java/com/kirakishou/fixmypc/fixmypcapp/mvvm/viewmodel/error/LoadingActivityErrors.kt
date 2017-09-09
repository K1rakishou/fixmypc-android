package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/8/2017.
 */
interface LoadingActivityErrors {
    fun onCouldNotConnectToServer(): Observable<Throwable>
    fun onUnknownError(): Observable<Throwable>
}