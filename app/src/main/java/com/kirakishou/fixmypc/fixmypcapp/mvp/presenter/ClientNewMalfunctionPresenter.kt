package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseCallbacks
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo

/**
 * Created by kirakishou on 7/27/2017.
 */
abstract class ClientNewMalfunctionPresenter<V : BaseCallbacks> : BaseActivityPresenter<V>() {
    abstract fun sendMalfunctionRequestToServer(malfunctionRequestInfo: MalfunctionRequestInfo)
}