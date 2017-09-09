package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class AppSettings {
    var userInfo: Fickle<UserInfo> = Fickle.empty()
    var accountType: Fickle<AccountType> = Fickle.empty()

    fun saveUserInfo(login: String, password: String, sessionId: String) {
        this.userInfo = Fickle.of(UserInfo(login, password, sessionId))
    }

    fun updateSessionId(sessionId: String) {
        userInfo.ifPresent {
            it.sessionId = sessionId
        }
    }
}