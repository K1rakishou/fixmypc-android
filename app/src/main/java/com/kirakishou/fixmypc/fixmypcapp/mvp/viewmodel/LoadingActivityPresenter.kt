package com.kirakishou.fixmypc.fixmypcapp.mvp.viewmodel

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityView

/**
 * Created by kirakishou on 7/22/2017.
 */
abstract class LoadingActivityPresenter<V : BaseActivityView> : BaseActivityPresenter<V>() {
    abstract fun startLoggingIn(login: String, password: String)
}