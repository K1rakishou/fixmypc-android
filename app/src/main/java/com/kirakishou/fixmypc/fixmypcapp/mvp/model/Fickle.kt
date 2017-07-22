package com.kirakishou.fixmypc.fixmypcapp.mvp.model

/**
 * Created by kirakishou on 7/21/2017.
 */
import java.util.*

class Fickle<out T> {

    private var value: T? = null

    private constructor() {
        this.value = null
    }

    private constructor(value: T) {
        this.value = Objects.requireNonNull(value)
    }

    interface Action<T> {
        fun apply(value: T)
    }

    fun isPresent(): Boolean {
        return value != null
    }

    fun  get(): T {
        return value!!
    }

    fun ifPresent(func: (v: T)-> Unit) {
        if (value != null) {
            func.invoke(value!!)
        }
    }

    companion object {
        fun <T> empty(): Fickle<T> {
            return Fickle()
        }

        fun <T> of(value: T?): Fickle<T> {
            if (value == null) {
                return empty()
            }

            return Fickle(value)
        }
    }
}