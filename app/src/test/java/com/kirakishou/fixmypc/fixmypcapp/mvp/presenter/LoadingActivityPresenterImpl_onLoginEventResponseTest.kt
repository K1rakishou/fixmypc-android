package com.kirakishou.fixmypc.fixmypcapp.mvp.presenter

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServerErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.view.LoadingActivityView
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by kirakishou on 7/29/2017.
 */

@RunWith(MockitoJUnitRunner::class)
class LoadingActivityPresenterImpl_onLoginEventResponseTest {

    @Mock
    lateinit var mViewCallbacks: LoadingActivityView

    @Mock
    lateinit var mEventBus: EventBus

    @InjectMocks
    lateinit var mPresenter: LoadingActivityPresenterImpl

    @Before
    fun setUp() {
        mPresenter.callbacks = mViewCallbacks
    }

    @Test
    fun shouldCallRunClientMainActivityWhenAccountTypeIsClient() {
        val sessionId = "1234567890abcdef"
        val accountType = AccountType.Client

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.Success(LoginResponse(sessionId, accountType, ServerErrorCode.SEC_OK))))

        Mockito.verify(mViewCallbacks, Mockito.only()).runClientMainActivity(sessionId, accountType)
    }

    @Test
    fun shouldCallRunSpecialistMainActivityWhenAccountTypeIsSpecialist() {
        val sessionId = "1234567890abcdef"
        val accountType = AccountType.Specialist

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.Success(LoginResponse(sessionId, accountType, ServerErrorCode.SEC_OK))))

        Mockito.verify(mViewCallbacks, Mockito.only()).runSpecialistMainActivity(sessionId, accountType)
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIllegalStateExceptionWhenAccountTypeIsGuest() {
        val sessionId = "1234567890abcdef"
        val accountType = AccountType.Guest

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.Success(LoginResponse(sessionId, accountType, ServerErrorCode.SEC_OK))))
    }

    @Test
    fun shouldCallRunGuestMainActivityWhenErrorCodeIsWrongLoginOrPassword() {
        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.ServerError(ServerErrorCode.SEC_WRONG_LOGIN_OR_PASSWORD)))

        Mockito.verify(mViewCallbacks, Mockito.only()).runGuestMainActivity()
    }

    @Test
    fun shouldCallRunGuestMainActivityWhenErrorCodeIsUnknownServerError() {
        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.ServerError(ServerErrorCode.SEC_UNKNOWN_SERVER_ERROR)))

        Mockito.verify(mViewCallbacks, Mockito.only()).runGuestMainActivity()
    }

    @Test
    fun shouldCallOnCouldNotConnectToServerWhenOccurredTimeoutException() {
        val exception = TimeoutException()

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.UnknownError(exception)))

        Mockito.verify(mViewCallbacks, Mockito.only()).onCouldNotConnectToServer(exception)
    }

    @Test
    fun shouldCallOnCouldNotConnectToServerWhenOccurredUnknownHostException() {
        val exception = UnknownHostException()

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.UnknownError(exception)))

        Mockito.verify(mViewCallbacks, Mockito.only()).onCouldNotConnectToServer(exception)
    }

    @Test
    fun shouldCallOnUnknownErrorWhenOccurredUnknownException() {
        val exception = RuntimeException()

        mPresenter.onLoginEventResponse(ServiceAnswer(ServiceMessageType.SERVICE_MESSAGE_LOGIN,
                ServerResponse.UnknownError(exception)))

        Mockito.verify(mViewCallbacks, Mockito.only()).onUnknownError(exception)
    }
}


































































