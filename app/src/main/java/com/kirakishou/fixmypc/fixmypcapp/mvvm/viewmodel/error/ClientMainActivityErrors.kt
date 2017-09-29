package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/29/2017.
 */
interface ClientMainActivityErrors {
    fun onBadResponse(): Observable<ErrorCode.Remote>
    fun onUnknownError(): Observable<Throwable>
}