package com.kirakishou.fixmypc.fixmypcapp.helper.rx

import io.reactivex.Observable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by kirakishou on 9/16/2017.
 */


class RxUtilsTest {

    @Test
    fun testStreamSplitter() {
        val observables = Observable.range(0, 100).observeOn(Schedulers.trampoline())
        val predicates1 = mutableListOf<Predicate<Int>>()
        val predicates2 = mutableListOf<Predicate<Int>>()
        val predicates3 = mutableListOf<Predicate<Int>>()

        predicates1 += Predicate { it % 2 == 0 }
        predicates1 += Predicate { it < 30 }

        predicates2 += Predicate { it % 3 == 0 }
        predicates2 += Predicate { it in 30..59 }

        predicates3 += Predicate { it % 5 == 0 }
        predicates3 += Predicate { it >= 60 }

        val split = RxUtils.splitRxStream(observables, predicates1, predicates2, predicates3)
        val transformed = mutableListOf<Observable<String>>()

        transformed += split[0].map { return@map "stream0: $it % 2 == 0 && $it < 30" }
        transformed += split[1].map { return@map "stream1: $it % 3 == 0 && it in 30..59" }
        transformed += split[2].map { return@map "stream2: $it % 5 == 0 && it >= 60" }

        Observable.merge(transformed)
                .subscribe({ println("value: $it") })
    }

    @Test
    fun test() {
        Observable.empty<Int>()
                .subscribeOn(Schedulers.trampoline())
                .switchIfEmpty { Observable.just(2) }
                .subscribe({ println("val: $it") })
    }
}



































