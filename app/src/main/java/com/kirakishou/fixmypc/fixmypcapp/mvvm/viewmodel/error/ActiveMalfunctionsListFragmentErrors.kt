package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.error

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ActiveMalfunctionsListFragmentErrors {
    //fun onNothingFoundSubject(): Observable<Unit>
    fun onUnknownError(): Observable<Throwable>
}