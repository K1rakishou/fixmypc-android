package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/8/2017.
 */
interface LoadingActivityErrors {
    fun onUnknownError(): Observable<Throwable>
    fun onBadResponse(): Observable<ErrorCode.Remote>
}