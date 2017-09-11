package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.helper.ProgressUpdate
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ClientNewMalfunctionActivityOutputs {
    fun onMalfunctionRequestSuccessfullyCreated(): Observable<Unit>
    fun uploadProgressUpdateSubject(): Observable<ProgressUpdate>
}