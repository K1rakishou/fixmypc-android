package com.kirakishou.fixmypc.fixmypcapp.helper.rx.operator

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions.DuplicateEntryException
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.fuseable.QueueFuseable
import io.reactivex.internal.observers.BasicFuseableObserver
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by kirakishou on 8/25/2017.
 */

/**
 *
 * Checks for duplicate values provided by valueSelector function and throws DuplicateObservableException if there are any
 *
 * */
class ErrorOnDuplicate<T, out V>(val valueSelector: (T) -> V) : ObservableOperator<T, T> {

    private val collection = hashSetOf<V>()

    override fun apply(observer: Observer<in T>): Observer<in T> {

        return object : BasicFuseableObserver<T, T>(observer) {

            override fun onNext(inValue: T) {
                if (done) {
                    return
                }

                if (sourceMode == QueueFuseable.NONE) {
                    val b: Boolean

                    try {
                        val value = ObjectHelper.requireNonNull<V>(valueSelector(inValue), "The keySelector returned a null key")
                        b = collection.add(value)
                    } catch (ex: Throwable) {
                        fail(ex)
                        return
                    }

                    if (b) {
                        actual.onNext(inValue)
                    } else {
                        //actual.onError(DuplicateObservableException())
                        fail(DuplicateEntryException())
                    }
                } else {
                    actual.onNext(null)
                }
            }

            override fun onError(e: Throwable) {
                if (done) {
                    RxJavaPlugins.onError(e)
                } else {
                    done = true
                    collection.clear()
                    actual.onError(e)
                }
            }

            override fun onComplete() {
                if (!done) {
                    done = true
                    collection.clear()
                    actual.onComplete()
                }
            }

            override fun requestFusion(mode: Int): Int {
                return transitiveBoundaryFusion(mode)
            }

            override fun poll(): T? {
                while (true) {
                    val value = qs.poll()

                    if (value == null || collection.add(ObjectHelper.requireNonNull<V>(valueSelector(value), "The keySelector returned a null key"))) {
                        return value
                    }
                }
            }

            override fun clear() {
                collection.clear()
                super.clear()
            }
        }
    }
}