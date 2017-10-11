package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.NewProfileInfo
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/10/2017.
 */
interface UpdateSpecialistProfileActivityOutputs {
    fun onUpdateSpecialistProfileResponseSubject(): Observable<Unit>
    fun onUpdateSpecialistProfileFragmentInfo(): Observable<NewProfileInfo>
    fun onUpdateSpecialistProfileFragmentPhoto(): Observable<String>
}