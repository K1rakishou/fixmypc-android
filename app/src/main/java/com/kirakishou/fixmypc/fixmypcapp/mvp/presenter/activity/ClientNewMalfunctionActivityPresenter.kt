package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.activity

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityPresenter
import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityView
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo

/**
 * Created by kirakishou on 7/27/2017.
 */
abstract class ClientNewMalfunctionActivityPresenter<V : BaseActivityView> : BaseActivityPresenter<V>() {
    abstract fun sendMalfunctionRequestToServer(malfunctionRequestInfo: MalfunctionRequestInfo)
}