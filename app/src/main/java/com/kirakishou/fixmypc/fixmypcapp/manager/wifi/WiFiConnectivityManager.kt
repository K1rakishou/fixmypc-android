package com.kirakishou.fixmypc.fixmypcapp.manager.wifi

import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
object WiFiConnectivityManager {

    val listeners: MutableMap<Class<*>, WiFiConnectivityObserver> = HashMap()
    var dispatchUpdates = true

    fun onWiFiChanged(isConnected: Boolean) {
        if (!dispatchUpdates) {
            return
        }

        synchronized(listeners) {
            for (listener in listeners) {
                listener.value.onWiFiConnectivityChanged(isConnected)
            }
        }
    }

    fun registerWifiListener(clazz: Class<*>, callback: WiFiConnectivityObserver) {
        synchronized(listeners) {
            if (listeners.contains(clazz)) {
                return
            }

            listeners.put(clazz, callback)
        }

        Timber.i("Registered listener: " + clazz.name)
    }

    fun removeWifiListener(clazz: Class<*>) {
        synchronized(listeners) {
            listeners.remove(clazz)
        }

        Timber.i("Removed listener: " + clazz.name)
    }
}