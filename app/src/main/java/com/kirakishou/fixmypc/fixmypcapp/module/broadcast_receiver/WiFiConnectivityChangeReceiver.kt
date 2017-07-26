package com.kirakishou.fixmypc.fixmypcapp.module.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kirakishou.fixmypc.fixmypcapp.manager.wifi.WiFiConnectivityManager
import com.kirakishou.fixmypc.fixmypcapp.util.NetUtils
import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
class WiFiConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        if (context == null) {
            Timber.i("WiFiReceiver.onReceive context == null")
            return
        }

        val wifiConnected = NetUtils.isWifiConnected(context)
        Timber.i("WiFiConnectivity changed: WiFiConnected == " + wifiConnected.toString())

        WiFiConnectivityManager.onWiFiChanged(wifiConnected)
    }
}