package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ClientNewMalfunctionActivityOutputs {
    fun onMalfunctionRequestSuccessfullyCreated(): Observable<Unit>
}