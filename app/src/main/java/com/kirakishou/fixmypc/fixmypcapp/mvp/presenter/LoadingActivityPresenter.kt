package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.base.BasePresenter
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessage

/**
 * Created by kirakishou on 7/22/2017.
 */
abstract class LoadingActivityPresenter<V : BaseCallbacks> : BasePresenter<V>() {
    abstract fun onStart()
    abstract fun onStop()
    abstract fun onEventAnswer(answer: ServiceAnswer)
    abstract fun sendServiceMessage(message: ServiceMessage)
}