package com.kirakishou.fixmypc.fixmypcapp.module.receiver

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundService
import com.kirakishou.fixmypc.fixmypcapp.util.AndroidUtils
import timber.log.Timber

/**
 * Created by kirakishou on 7/21/2017.
 */
class BootBroadcastReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("RECEIVE_BOOT_COMPLETED received")

        if (AndroidUtils.isLollipopOrHigher()) {
            Timber.w("We don't need WakefulBroadcastReceiver on Lollipop or higher since we have JobScheduler here")
            return
        }

        val startServiceIntent = Intent(context, BackgroundService::class.java)
        WakefulBroadcastReceiver.startWakefulService(context, startServiceIntent)
    }
}