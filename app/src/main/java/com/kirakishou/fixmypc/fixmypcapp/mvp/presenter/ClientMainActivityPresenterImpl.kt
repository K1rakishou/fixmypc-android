package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionRequestInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceMessage
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.ClientMainActivityView
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kirakishou on 7/27/2017.
 */
open class ClientMainActivityPresenterImpl
    @Inject constructor(protected val mEventBus: EventBus): ClientMainActivityPresenter<ClientMainActivityView>() {

    override fun initPresenter() {
        Timber.d("ClientMainActivityPresenterImpl.initPresenter()")
    }

    override fun destroyPresenter() {
        Timber.d("ClientMainActivityPresenterImpl.destroyPresenter()")
    }

    override fun sendServiceMessage(message: ServiceMessage) {

    }

    override fun sendMalfunctionRequestToServer(malfunctionRequestInfo: MalfunctionRequestInfo) {
        //TODO: send request to the service via eventBus
    }

    override fun onEventAnswer(answer: ServiceAnswer) {

    }
}