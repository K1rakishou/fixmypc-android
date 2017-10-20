package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output

import io.reactivex.Observable

/**
 * Created by kirakishou on 9/9/2017.
 */
interface ClientNewDamageClaimActivityOutputs {
    fun onMalfunctionRequestSuccessfullyCreated(): Observable<Unit>
}