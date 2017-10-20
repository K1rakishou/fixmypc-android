package com.kirakishou.fixmypc.fixmypcapp.mvvm.viewmodel.wires.output

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.packet.ClientProfilePacket
import io.reactivex.Observable

/**
 * Created by kirakishou on 10/20/2017.
 */
interface UpdateClientProfileActivityOutputs {
    fun onUpdateClientProfileFragmentUiInfo(): Observable<ClientProfilePacket>
    fun onUpdateProfileInfoResponse(): Observable<Unit>
}