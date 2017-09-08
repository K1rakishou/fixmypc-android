package com.kirakishou.fixmypc.fixmypcapp.helper.annotation

import kotlin.reflect.KClass

/**
 * Created by kirakishou on 9/8/2017.
 */

@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresViewModel(val viewModelClass: KClass<*>)