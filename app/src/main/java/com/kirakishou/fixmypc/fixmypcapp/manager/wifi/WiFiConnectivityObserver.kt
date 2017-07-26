package com.kirakishou.fixmypc.fixmypcapp.manager.wifi

/**
 * Created by kirakishou on 7/26/2017.
 */
interface WiFiConnectivityObserver {
    fun onWiFiConnectivityChanged(isConnected: Boolean)
}