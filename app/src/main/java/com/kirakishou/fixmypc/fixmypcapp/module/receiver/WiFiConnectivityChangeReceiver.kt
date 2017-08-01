package com.kirakishou.fixmypc.fixmypcapp.module.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.kirakishou.fixmypc.fixmypcapp.manager.wifi.WiFiConnectivityManager
import com.kirakishou.fixmypc.fixmypcapp.util.AndroidUtils
import com.kirakishou.fixmypc.fixmypcapp.util.NetUtils
import timber.log.Timber

/**
 * Created by kirakishou on 7/26/2017.
 */
class WiFiConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (AndroidUtils.isLollipopOrHigher()) {
            Timber.w("We don't need WifI BroadcastReceiver on Lollipop or higher since we have JobScheduler here")
            return
        }

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

        WiFiConnectivityManager.onWiFiChanged(wifiConnected)
    }
}