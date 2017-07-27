package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType

/**
 * Created by kirakishou on 7/22/2017.
 */
data class ServiceAnswer(val type: ServiceMessageType,
                         val data: ServerResponse<Any>)