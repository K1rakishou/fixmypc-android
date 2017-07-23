package com.kirakishou.fixmypc.fixmypcapp.base

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage

/**
 * Created by kirakishou on 7/23/2017.
 */
abstract class BaseActivityPresenter<V : BaseCallbacks> : BasePresenter<V>() {
    abstract fun onInitPresenter()
    abstract fun onTeardownPresenter()
    abstract fun onEventAnswer(answer: ServiceAnswer)
    abstract fun sendServiceMessage(message: ServiceMessage)
}