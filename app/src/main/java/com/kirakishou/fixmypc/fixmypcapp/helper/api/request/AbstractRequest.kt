package com.kirakishou.fixmypc.fixmypcapp.helper.api.request

import android.support.annotation.CallSuper
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response.IsProfileFilledInResponse
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by kirakishou on 9/12/2017.
 */
abstract class AbstractRequest<out T> {
    abstract fun build(): T

    fun logError(error: Throwable) {
        Timber.e(error)
    }
}