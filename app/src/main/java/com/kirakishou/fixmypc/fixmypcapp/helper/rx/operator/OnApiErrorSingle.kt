package com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response.StatusResponse
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.exceptions.ApiException
import io.reactivex.SingleObserver
import io.reactivex.SingleOperator
import io.reactivex.disposables.Disposable
import retrofit2.Response

/**
 * Created by kirakishou on 8/25/2017.
 */

/**
 *
 * Whenever HttpException occurs returns converted errorBody as an ApiException with ErrorCode.Remote and HttpStatus
 *
 * */
class OnApiErrorSingle<T>(val gson: Gson) : SingleOperator<T, Response<T>> {

    override fun apply(observer: SingleObserver<in T>): SingleObserver<in Response<T>> {

        return object : SingleObserver<Response<T>> {
            override fun onError(e: Throwable) {
                observer.onError(e)
            }

            override fun onSubscribe(d: Disposable) {
                observer.onSubscribe(d)
            }

            override fun onSuccess(response: Response<T>) {
                if (!response.isSuccessful) {
                    try {
                        val responseJson = response.errorBody()!!.string()
                        val error = gson.fromJson<StatusResponse>(responseJson, StatusResponse::class.java)

                        observer.onError(ApiException(error.errorCode, response.code()))
                    } catch (e: Exception) {
                        observer.onError(e)
                    }
                } else {
                    observer.onSuccess(response.body()!!)
                }
            }
        }
    }
}