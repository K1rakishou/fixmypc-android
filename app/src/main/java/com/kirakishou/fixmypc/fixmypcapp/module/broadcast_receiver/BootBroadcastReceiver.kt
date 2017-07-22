package com.kirakishou.fixmypc.fixmypcapp.module.broadcast_receiver

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.kirakishou.fixmypc.fixmypcapp.module.service.BackgroundService
import timber.log.Timber

/**
 * Created by kirakishou on 7/21/2017.
 */
class BootBroadcastReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("RECEIVE_BOOT_COMPLETED received")

        /*val startServiceIntent = Intent(context, BackgroundService::class.java)
        WakefulBroadcastReceiver.startWakefulService(context, startServiceIntent)*/
    }
}