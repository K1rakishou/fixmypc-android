package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.ClientProfileResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by kirakishou on 9/20/2017.
 */
class GetClientProfileRequest(protected val mUserId: Long,
                              protected val mApiService: ApiService,
                              protected val mGson: Gson) : AbstractRequest<Single<ClientProfileResponse>> {

    override fun execute(): Single<ClientProfileResponse> {
        return mApiService.getClientProfile(mUserId)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
    }
}