package com.kirakishou.fixmypc.fixmypcapp.module.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.kirakishou.fixmypc.fixmypcapp.store.wifi.WiFiConnectivityStore
import com.kirakishou.fixmypc.fixmypcapp.util.NetUtils
import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
class WiFiConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Timber.i("WiFiReceiver.onReceive context == null || intent == null")
            return
        }

        if (intent.action != WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION &&
                intent.action != WifiManager.NETWORK_STATE_CHANGED_ACTION) {

            return
        }

        val wifiConnected = NetUtils.isWifiConnected(context)
        Timber.i("WiFiConnectivity changed: WiFiConnected == " + wifiConnected.toString())

        WiFiConnectivityStore.onWiFiChanged(wifiConnected)
    }
}