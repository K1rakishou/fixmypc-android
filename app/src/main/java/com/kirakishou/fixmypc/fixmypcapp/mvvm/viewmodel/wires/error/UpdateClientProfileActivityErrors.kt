package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/20/2017.
 */
interface UpdateClientProfileActivityErrors {
    fun onBadResponse(): Observable<ErrorCode.Remote>
    fun onUnknownError(): Observable<Throwable>
}