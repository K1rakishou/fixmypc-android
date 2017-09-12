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
class GetDamageClaimRequest(protected val lat: Double,
                            protected val lon: Double,
                            protected val radius: Double,
                            protected val page: Long,
                            protected val mApiService: ApiService,
                            protected val mGson: Gson) : AbstractRequest<Single<DamageClaimsResponse>> {
    override fun execute(): Single<DamageClaimsResponse> {
        return mApiService.getDamageClaims(lat, lon, radius, page)
                .subscribeOn(Schedulers.io())
                .lift(OnApiErrorSingle(mGson))
    }
}