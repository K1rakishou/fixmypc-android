package com.kirakishou.fixmypc.fixmypcapp.mvp.view

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse

/**
 * Created by kirakishou on 7/20/2017.
 */
interface LoadingActivityView : BaseView {
    fun onLoggedIn(loginResponse: LoginResponse)
}