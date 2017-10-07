package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginResponseDataDTO
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/6/2017.
 */
interface LoginActivityOutputs {
    fun runClientMainActivity(): Observable<LoginResponseDataDTO>
    fun runSpecialistMainActivity(): Observable<LoginResponseDataDTO>
}