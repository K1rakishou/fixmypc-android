package com.kirakishou.fixmypc.fixmypcapp.helper.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import com.kirakishou.fixmypc.fixmypcapp.helper.util.AndroidUtils

/**
 * Created by kirakishou on 9/12/2017.
 */

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.myGetDrawable(id: Int): Drawable {
    return if (AndroidUtils.isAtleastLollipop()) {
        resources.getDrawable(id, theme)
    } else {
        resources.getDrawable(id)
    }
}