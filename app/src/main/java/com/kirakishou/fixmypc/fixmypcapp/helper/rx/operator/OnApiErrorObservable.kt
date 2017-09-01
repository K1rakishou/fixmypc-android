package com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ApiException
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.Response

/**
 * Created by kirakishou on 8/27/2017.
 */
class OnApiErrorObservable<T>(val gson: Gson) : ObservableOperator<T, Response<T>> {

    override fun apply(observer: Observer<in T>): Observer<in Response<T>> {
        return object : Observer<Response<T>> {

            override fun onSubscribe(d: Disposable) {
                observer.onSubscribe(d)
            }

            override fun onNext(response: Response<T>) {
                if (!response.isSuccessful) {
                    try {
                        val responseJson = response.errorBody()!!.string()
                        val error = gson.fromJson<StatusResponse>(responseJson, StatusResponse::class.java)

                        observer.onError(ApiException(error.errorCode, response.code()))
                    } catch (e: Exception) {
                        observer.onError(e)
                    }
                } else {
                    observer.onNext(response.body()!!)
                }
            }

            override fun onError(e: Throwable) {
                observer.onError(e)
            }

            override fun onComplete() {
                observer.onComplete()
            }
        }
    }
}