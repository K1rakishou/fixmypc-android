package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class AppSettings {
    private var userInfo: Fickle<UserInfo> = Fickle.empty()
    private var accountType: Fickle<AccountType> = Fickle.empty()

    @Synchronized
    fun saveUserInfo(login: String, password: String, sessionId: String) {
        this.userInfo = Fickle.of(UserInfo(login, password, sessionId))
    }

    //check if userinfo exists first!
    @Synchronized
    fun loadUserInfo(): UserInfo {
        return userInfo.get()
    }

    @Synchronized
    fun isUserInfoExists(): Boolean {
        return userInfo.isPresent()
    }

    @Synchronized
    fun updateSessionId(sessionId: String) {
        userInfo.ifPresent {
            it.sessionId = sessionId
        }
    }
}