package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/26/2017.
 */
class AppSettings {
    var sessionId: Fickle<String> = Fickle.empty()
    var accountType: Fickle<AccountType> = Fickle.empty()
}