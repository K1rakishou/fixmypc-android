package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks

/**
 * Created by kirakishou on 7/22/2017.
 */
abstract class LoadingActivityPresenter<V : BaseCallbacks> : BaseActivityPresenter<V>() {
    abstract fun startLoggingIn(login: String, password: String)
}