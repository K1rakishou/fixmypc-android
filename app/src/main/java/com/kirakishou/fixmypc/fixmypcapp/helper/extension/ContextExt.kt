package com.kirakishou.fixmypc.fixmypcapp.helper.extension

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by kirakishou on 9/12/2017.
 */

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager