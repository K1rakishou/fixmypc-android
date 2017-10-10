package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/10/2017.
 */
interface UpdateSpecialistProfileActivityErrors {
    fun onBadResponse(): Observable<ErrorCode.Remote>
    fun onUnknownError(): Observable<Throwable>
}