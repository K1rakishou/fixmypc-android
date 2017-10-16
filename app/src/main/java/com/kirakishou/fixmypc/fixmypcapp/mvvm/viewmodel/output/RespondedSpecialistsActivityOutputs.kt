package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.SpecialistProfile
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.AssignSpecialistResponse
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.StatusResponse
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/2/2017.
 */
interface RespondedSpecialistsActivityOutputs {
    fun onSpecialistsListResponse(): Observable<List<SpecialistProfile>>
    fun onAssignSpecialistResponse(): Observable<AssignSpecialistResponse>
}