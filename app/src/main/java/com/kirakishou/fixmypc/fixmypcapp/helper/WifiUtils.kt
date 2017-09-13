package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.Context
import android.net.ConnectivityManager
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.connectivityManager


/**
 * Created by kirakishou on 9/13/2017.
 */
class WifiUtils(protected val mContext: Context) {

    fun isWifiConnected(): Boolean {
        val connManager = mContext.connectivityManager
        val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        return wifi.isConnected
    }
}