package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.NewProfileInfo
import com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.UpdateSpecialistProfileActivityViewModel
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/10/2017.
 */
interface UpdateSpecialistProfileActivityOutputs {
    fun onUpdateSpecialistProfileResponseSubject(): Observable<UpdateSpecialistProfileActivityViewModel.ProfileUpdate>
    fun onUpdateSpecialistProfileFragmentUiInfo(): Observable<NewProfileInfo>
    fun onUpdateSpecialistProfileFragmentPhoto(): Observable<String>
}