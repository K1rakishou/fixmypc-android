package com.kirakishou.fixmypc.fixmypcapp.mvp.view

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType

/**
 * Created by kirakishou on 7/20/2017.
 */
interface LoadingActivityView : BaseView {
    fun runClientMainActivity(sessionId: String, accountType: AccountType)
    fun runSpecialistMainActivity(sessionId: String, accountType: AccountType)
    fun runGuestMainActivity()
    fun onCouldNotConnectToServer(error: Throwable)
}