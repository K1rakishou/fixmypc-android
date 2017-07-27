package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ServiceMessageType

/**
 * Created by kirakishou on 7/21/2017.
 */
class ServiceMessage(val type: ServiceMessageType,
                     val data: Any)