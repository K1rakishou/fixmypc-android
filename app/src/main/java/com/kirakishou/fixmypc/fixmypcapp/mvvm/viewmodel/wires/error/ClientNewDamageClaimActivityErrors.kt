package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.error

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ClientNewDamageClaimActivityErrors {
    fun onBadResponse(): Observable<ErrorCode.Remote>
    fun onUnknownError(): Observable<Throwable>
}