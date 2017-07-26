package com.kirakishou.fixmypc.fixmypcapp.base

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage

/**
 * Created by kirakishou on 7/23/2017.
 */
abstract class BaseServicePresenter<V : BaseCallbacks> : BasePresenter<V>() {
    abstract fun onClientMessage(message: ServiceMessage)
    abstract fun sendClientAnswer(answer: ServiceAnswer)
}