package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.input

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.dto.LoginPasswordDTO

/**
 * Created by kirakishou on 10/6/2017.
 */
interface LoginActivityInputs {
    fun startLoggingIn(params: LoginPasswordDTO)
}