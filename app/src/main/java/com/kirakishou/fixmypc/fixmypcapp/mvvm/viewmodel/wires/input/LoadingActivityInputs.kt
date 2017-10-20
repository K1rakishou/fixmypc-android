package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO

/**
 * Created by kirakishou on 9/8/2017.
 */
interface LoadingActivityInputs {
    fun startLoggingIn(params: LoginPasswordDTO)
}