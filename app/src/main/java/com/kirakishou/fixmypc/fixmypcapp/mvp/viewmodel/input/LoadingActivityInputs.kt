package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel.input

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.dto.LoginPasswordDTO

/**
 * Created by kirakishou on 9/8/2017.
 */
interface LoadingActivityInputs {
    fun startLoggingIn(params: LoginPasswordDTO)
}