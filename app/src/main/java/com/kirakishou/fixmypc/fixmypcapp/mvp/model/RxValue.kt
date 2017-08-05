package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 8/5/2017.
 */

class RxValue<T> {
    var value: Fickle<T> = Fickle.empty()
    var error: Fickle<ErrorCode.Local> = Fickle.empty()

    private constructor()

    private constructor(value: T) {
        this.value = Fickle.of(value)
    }

    private constructor(error: ErrorCode.Local) {
        this.error = Fickle.of(error)
    }

    fun isValue(): Boolean {
        return value.isPresent()
    }

    fun isError(): Boolean {
        return error.isPresent()
    }

    companion object {
        fun <T> value(value: T): RxValue<T> {
            return RxValue(value)
        }

        fun <T> error(error: ErrorCode.Local): RxValue<T> {
            return RxValue(error)
        }
    }
}