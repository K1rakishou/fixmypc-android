package com.kirakishou.fixmypc.fixmypcapp.helper.extension

/**
 * Created by kirakishou on 10/21/2017.
 */

inline fun <T> Iterable<T>.firstOrDefault(default: T, predicate: (T) -> Boolean): T {
    for (element in this) {
        if (predicate(element)) {
            return element
        }
    }

    return default
}