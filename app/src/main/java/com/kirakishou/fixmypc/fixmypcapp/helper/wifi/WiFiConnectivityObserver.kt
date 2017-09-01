package com.kirakishou.fixmypc.fixmypcapp.helper.wifi

/**
 * Created by kirakishou on 7/26/2017.
 */
interface WiFiConnectivityObserver {
    fun onWiFiConnectivityChanged(isConnected: Boolean)
}