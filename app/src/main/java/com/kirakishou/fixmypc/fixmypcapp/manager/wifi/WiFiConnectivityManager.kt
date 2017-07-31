package com.kirakishou.fixmypc.fixmypcapp.manager.wifi

import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
object WiFiConnectivityManager {

    val listeners: MutableSet<WiFiConnectivityObserver> = HashSet()
    var dispatchUpdates = true

    fun onWiFiChanged(isConnected: Boolean) {
        if (!dispatchUpdates) {
            return
        }

        synchronized(listeners) {
            for (listener in listeners) {
                listener.onWiFiConnectivityChanged(isConnected)
            }
        }
    }

    fun registerWifiListener(callback: WiFiConnectivityObserver) {
        synchronized(listeners) {
            listeners.add(callback)
        }

        Timber.i("Registered listener: " + callback.javaClass.simpleName)
    }

    fun removeWifiListener(callback: WiFiConnectivityObserver) {
        synchronized(listeners) {
            listeners.remove(callback)
        }

        Timber.i("Removed listener: " + callback.javaClass.simpleName)
    }
}