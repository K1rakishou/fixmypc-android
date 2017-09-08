package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.output

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.LoginResponseDataDTO
import io.reactivex.Observable

/**
 * Created by kirakishou on 9/8/2017.
 */
interface LoadingActivityOutputs {
    fun runClientMainActivity(): Observable<LoginResponseDataDTO>
    fun runSpecialistMainActivity(): Observable<LoginResponseDataDTO>
    fun runGuestMainActivity(): Observable<Boolean>
}