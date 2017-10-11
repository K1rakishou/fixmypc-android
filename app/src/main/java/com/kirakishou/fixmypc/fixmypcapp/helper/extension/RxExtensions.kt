package com.kirakishou.fixmypc.fixmypcapp.helper.extension

import com.kirakishou.fixmypc.fixmypcapp.helper.rx.RxUtils
import io.reactivex.Observable
import io.reactivex.functions.Predicate

/**
 * Created by kirakishou on 10/9/2017.
 */

fun <T> Observable<T>.splitStream(vararg predicateArray: List<Predicate<T>>): List<Observable<T>> {
    return RxUtils.splitRxStream(this, *predicateArray)
}