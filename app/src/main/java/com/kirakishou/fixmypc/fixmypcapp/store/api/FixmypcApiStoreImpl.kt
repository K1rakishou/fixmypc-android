package com.kirakishou.fixmypc.fixmypcapp.store.api

import com.kirakishou.fixmypc.fixmypcapp.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.RxValue
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.MalfunctionApplicationInfo
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServerResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.ServiceAnswer
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.request.LoginRequest
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.LoginResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.presenter.BackgroundServicePresenter
import com.kirakishou.fixmypc.fixmypcapp.util.converter.ErrorBodyConverter
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by kirakishou on 7/22/2017.
 */
class FixmypcApiStoreImpl
    @Inject constructor(val mApiService: ApiService,
                        val errorBodyConverter: ErrorBodyConverter) : FixmypcApiStore {

    override var callbacks: Fickle<BackgroundServicePresenter> = Fickle.empty()
    private val mCompositeDisposable = CompositeDisposable()

    override fun cleanup() {
        mCompositeDisposable.clear()
    }

    override fun loginRequest(loginRequest: LoginRequest, type: ServiceMessageType) {
        mCompositeDisposable += mApiService.doLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ answer ->
                    callbacks.ifPresent {
                        it.returnAnswer(ServiceAnswer(type, ServerResponse.Success(answer)))
                    }
                }, { error ->
                    if (error is HttpException) {
                        val response = errorBodyConverter.convert<LoginResponse>(error.response().errorBody()!!.string(), LoginResponse::class.java)
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.ServerError(response.error)))
                        }
                    } else {
                        callbacks.ifPresent {
                            it.returnAnswer(ServiceAnswer(type, ServerResponse.UnknownError(error)))
                        }
                    }
                })
    }

    override fun sendMalfunctionRequest(malfunctionApplicationInfo: MalfunctionApplicationInfo, type: ServiceMessageType) {
        mCompositeDisposable += Single.just(malfunctionApplicationInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { requestInfo ->
                    if (!requestInfo.malfunctionPhotos.isPresent()) {
                        return@flatMap Single.just(RxValue.error<MalfunctionApplicationInfo>(ErrorCode.Local.LEC_MAI_PHOTOS_ARE_NOT_SET))
                    }

                    if (!requestInfo.malfunctionCategory.isPresent()) {
                        return@flatMap Single.just(RxValue.error<MalfunctionApplicationInfo>(ErrorCode.Local.LEC_MAI_CATEGORY_IS_NOT_SET))
                    }

                    if (!requestInfo.malfunctionDescription.isPresent()) {
                        return@flatMap Single.just(RxValue.error<MalfunctionApplicationInfo>(ErrorCode.Local.LEC_MAI_DESCRIPTION_IS_NOT_SET))
                    }

                    

                    return@flatMap Single.just(RxValue.value(requestInfo))
                }
                .subscribe({ answer ->

                }, { error ->

                })
    }
}