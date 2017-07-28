package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.AccountType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.StatusCode
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
    fun loginRequest_testWithBadLoginRequest_expectServerResponseHttpErrorWith_StatusWrongLoginOrPassword() {
        val BAD_LOGIN_REQUEST = LoginRequest("test2@gmail.com", "1234567890")
        val BAD_SESSION_ID = ""
        val BAD_RESPONSE = LoginResponse(BAD_SESSION_ID, AccountType.Client.value, StatusCode.STATUS_WRONG_LOGIN_OR_PASSWORD)
        val TYPE = ServiceMessageType.SERVICE_MESSAGE_LOGIN
        val BAD_LOGIN_RESPONSE_JSON = """{ "session_id": "", "account_type": 2, "status_code": 2 }"""

        Mockito.`when`(errorBodyConverter.convert<LoginResponse>(BAD_LOGIN_RESPONSE_JSON, LoginResponse::class.java)).thenReturn(BAD_RESPONSE)
        Mockito.`when`(mApiService.doLogin(BAD_LOGIN_REQUEST)).thenReturn(Single.error(HttpException(
                Response.error<LoginResponse>(422, ResponseBody.create(MediaType.parse("application/json"), BAD_LOGIN_RESPONSE_JSON)))))

        mApiStore.loginRequest(BAD_LOGIN_REQUEST, TYPE)

        Mockito.verify(callbacks).returnAnswer(ServiceAnswer(TYPE, ServerResponse.HttpError(StatusCode.STATUS_WRONG_LOGIN_OR_PASSWORD)))
    }

    @Test
    fun loginRequest_testWithGoodLoginRequest_expectServerResponseSuccess() {
        val GOOD_LOGIN_REQUEST = LoginRequest("test@gmail.com", "1234567890")
        val GOOD_SESSION_ID = "1234567890abcdef"
        val TYPE = ServiceMessageType.SERVICE_MESSAGE_LOGIN
        val GOOD_RESPONSE = LoginResponse(GOOD_SESSION_ID, AccountType.Client.value, StatusCode.STATUS_OK)

        Mockito.`when`(mApiService.doLogin(GOOD_LOGIN_REQUEST)).thenReturn(Single.just(GOOD_RESPONSE))

        mApiStore.loginRequest(GOOD_LOGIN_REQUEST, TYPE)

        Mockito.verify(callbacks).returnAnswer(ServiceAnswer(TYPE, ServerResponse.Success(GOOD_RESPONSE)))
    }
}















































