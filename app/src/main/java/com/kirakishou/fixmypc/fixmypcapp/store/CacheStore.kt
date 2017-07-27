package com.kirakishou.fixmypc.fixmypcapp.store

import com.google.common.cache.Cache
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle

/**
 * Created by kirakishou on 7/26/2017.
 */
class CacheStore<K, V>(val cache: Cache<K, V>) {

    fun put(key: K, value: V) {
        cache.put(key, value)
    }

    fun invalidate(key: K) {
        cache.invalidate(key)
    }

    fun get(key: K): Fickle<V> {
        return Fickle.of(cache.getIfPresent(key))
    }

    fun contains(key: K): Boolean {
        return cache.getIfPresent(key) != null
    }

    fun size(): Long {
        return cache.size()
    }
}