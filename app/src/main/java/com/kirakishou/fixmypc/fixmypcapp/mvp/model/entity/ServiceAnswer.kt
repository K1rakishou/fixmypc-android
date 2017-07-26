package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity

/**
 * Created by kirakishou on 7/22/2017.
 */
data class ServiceAnswer(val type: ServiceMessageType,
                         val data: Fickle<Any>)