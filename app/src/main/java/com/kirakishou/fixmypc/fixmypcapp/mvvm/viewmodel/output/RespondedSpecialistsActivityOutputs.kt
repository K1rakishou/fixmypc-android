package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/2/2017.
 */
interface RespondedSpecialistsActivityOutputs {
    fun mOnSpecialistsListResponse(): Observable<List<SpecialistProfile>>
}