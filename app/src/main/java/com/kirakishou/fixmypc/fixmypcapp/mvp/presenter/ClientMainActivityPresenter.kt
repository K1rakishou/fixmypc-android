package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks

/**
 * Created by kirakishou on 7/27/2017.
 */
abstract class ClientMainActivityPresenter<V : BaseCallbacks> : BaseActivityPresenter<V>() {
    abstract fun sendApplicationToServer()
}