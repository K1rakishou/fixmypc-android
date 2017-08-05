package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.*
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


/**
 * Created by kirakishou on 7/27/2017.
 */

@RunWith(MockitoJUnitRunner::class)
class FixmypcApiStoreImpl_LoginRequestTest {

    @Mock
    lateinit var mApiService: ApiService

    @Mock
    lateinit var callbacks: BackgroundServicePresenter

    @Mock
    lateinit var errorBodyConverter: ErrorBodyConverter

    @InjectMocks
    lateinit var mApiStore: FixmypcApiStoreImpl

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { _ ->
            Schedulers.trampoline()
        }

        mApiStore.callbacks = Fickle.of(callbacks)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun shouldCallReturnAnswerWithServerResponseHttpError() {
        val badLoginRequest = LoginRequest("test2@gmail.com", "1234567890")
        val badSessionId = ""
        val badResponse = LoginResponse(badSessionId, AccountType.Client, ErrorCode.SEC_WRONG_LOGIN_OR_PASSWORD)
        val type = ServiceMessageType.SERVICE_MESSAGE_LOGIN
        val badLoginResponseJson = """{ "session_id": "", "account_type": 2, "status_code": 2 }"""

        Mockito.`when`(errorBodyConverter.convert<LoginResponse>(badLoginResponseJson, LoginResponse::class.java)).thenReturn(badResponse)
        Mockito.`when`(mApiService.doLogin(badLoginRequest)).thenReturn(Single.error(HttpException(
                Response.error<LoginResponse>(HttpStatus.UNPROCESSABLE_ENTITY.status, ResponseBody.create(MediaType.parse("application/json"), badLoginResponseJson)))))

        mApiStore.loginRequest(badLoginRequest, type)

        Mockito.verify(callbacks, Mockito.only())
                .returnAnswer(ServiceAnswer(type, ServerResponse.ServerError(ErrorCode.SEC_WRONG_LOGIN_OR_PASSWORD)))
    }

    @Test
    fun shouldCallReturnAnswerWithServerResponseSuccess() {
        val goodLoginRequest = LoginRequest("test@gmail.com", "1234567890")
        val goodSessionId = "1234567890abcdef"
        val type = ServiceMessageType.SERVICE_MESSAGE_LOGIN
        val goodResponse = LoginResponse(goodSessionId, AccountType.Client, ErrorCode.SEC_OK)

        Mockito.`when`(mApiService.doLogin(goodLoginRequest)).thenReturn(Single.just(goodResponse))

        mApiStore.loginRequest(goodLoginRequest, type)

        Mockito.verify(callbacks, Mockito.only())
                .returnAnswer(ServiceAnswer(type, ServerResponse.Success(goodResponse)))
    }

    @Test
    fun shouldCallReturnAnswerWithServerResponseUnknownError() {
        val triggerExceptionLoginRequest = LoginRequest("trigger_exception@gmail.com", "1234567890")
        val type = ServiceMessageType.SERVICE_MESSAGE_LOGIN
        val exception = IOException("Something went wrong")

        Mockito.`when`(mApiService.doLogin(triggerExceptionLoginRequest)).thenReturn(Single.error<LoginResponse>(exception))

        mApiStore.loginRequest(triggerExceptionLoginRequest, type)

        Mockito.verify(callbacks, Mockito.only())
                .returnAnswer(ServiceAnswer(type, ServerResponse.UnknownError(exception)))
    }
}















































