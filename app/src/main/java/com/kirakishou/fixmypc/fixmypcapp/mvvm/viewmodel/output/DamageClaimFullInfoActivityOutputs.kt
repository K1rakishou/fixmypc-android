package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.output

import io.reactivex.Observable

/**
 * Created by kirakishou on 10/15/2017.
 */
interface DamageClaimFullInfoActivityOutputs {
    fun onRespondToDamageClaimSuccessSubject(): Observable<Unit>
    fun onHasAlreadyRespondedResponse(): Observable<Boolean>
    fun onNotifyProfileIsNotFilledIn(): Observable<Unit>
}