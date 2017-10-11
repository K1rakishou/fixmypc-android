package com.kirakishou.fixmypc.fixmypcapp.helper.extension

import android.support.v4.app.Fragment

/**
 * Created by kirakishou on 10/11/2017.
 */

fun Fragment.hideKeyboard() {
    this.activity.hideKeyboard()
}