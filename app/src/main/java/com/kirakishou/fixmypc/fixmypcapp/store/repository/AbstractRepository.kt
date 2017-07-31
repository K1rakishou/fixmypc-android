package com.kirakishou.fixmypc.fixmypcapp.store.repository

import com.google.common.cache.CacheBuilder
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.store.cache.CacheStore
import com.kirakishou.fixmypc.fixmypcapp.manager.wifi.WiFiConnectivityObserver
import com.kirakishou.fixmypc.fixmypcapp.manager.wifi.WiFiConnectivityManager
import java.util.concurrent.TimeUnit

/**
 * Created by kirakishou on 7/26/2017.
 */
abstract class AbstractRepository<T> : WiFiConnectivityObserver {

    lateinit var cacheStore: CacheStore<String, T>
    private var isConnectedToWiFi = false

    private constructor()

    constructor(isConnectedToWiFi: Boolean) {
        this.isConnectedToWiFi = isConnectedToWiFi

        cacheStore = CacheStore(CacheBuilder.newBuilder()
                .maximumSize(150)
                .expireAfterAccess(20, TimeUnit.MINUTES)
                .build<String, T>())
    }

    fun init() {
        WiFiConnectivityManager.registerWifiListener(this)
    }

    fun destroy() {
        WiFiConnectivityManager.removeWifiListener(this)
    }

    override fun onWiFiConnectivityChanged(isConnected: Boolean) {
        isConnectedToWiFi = isConnected
    }

    fun isCached(key: String): Boolean {
        return cacheStore.contains(key)
    }

    fun cache(key: String, value: T) {
        cacheStore.put(key, value)
    }

    fun fromCache(key: String): Fickle<T> {
        return cacheStore.get(key)
    }


}