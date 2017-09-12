package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import com.google.gson.Gson
import com.kirakishou.fixmypc.fixmypcapp.helper.api.ApiService
import com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator.OnApiErrorSingle
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.DamageClaimsResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by kirakishou on 9/12/2017.
 */
class GetDamageClaimRequest(protected val mLat: Double,
                            protected val mLon: Double,
                            protected val mRadius: Double,
                            protected val mPage: Long,
                            protected val mApiService: ApiService,
                            protected val mGson: Gson) : AbstractRequest<Single<DamageClaimsResponse>> {
    override fun execute(): Single<DamageClaimsResponse> {
        return mApiService.getDamageClaims(mLat, mLon, mRadius, mPage)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
    }
}