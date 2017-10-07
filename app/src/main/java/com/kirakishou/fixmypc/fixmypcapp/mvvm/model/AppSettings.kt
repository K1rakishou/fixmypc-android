package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class AppSettings {
    private var userInfoFickle: Fickle<UserInfo> = Fickle.empty()
    private var accountTypeFickle: Fickle<AccountType> = Fickle.empty()

    @Synchronized
    fun saveUserInfo(login: String, password: String, sessionId: String) {
        this.userInfoFickle = Fickle.of(UserInfo(login, password, sessionId))
    }

    @Synchronized
    fun saveUserInfo(userInfo: UserInfo) {
        this.userInfoFickle = Fickle.of(userInfo)
    }

    @Synchronized
    fun updateSessionId(sessionId: String) {
        if (!userInfoFickle.isPresent()) {
            throw IllegalStateException("userInfoFickle does not exist")
        }

        userInfoFickle.get().sessionId = sessionId
    }

    @Synchronized
    fun clearUserInfo() {
        userInfoFickle = Fickle.empty()
    }

    //check if userinfo exists first!
    @Synchronized
    fun loadUserInfo(): UserInfo {
        return userInfoFickle.get()
    }

    @Synchronized
    fun isUserInfoExists(): Boolean {
        return userInfoFickle.isPresent()
    }
}