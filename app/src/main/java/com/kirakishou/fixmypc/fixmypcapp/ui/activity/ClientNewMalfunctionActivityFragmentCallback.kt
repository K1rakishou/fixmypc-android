package com.kirakishou.fixmypc.fixmypcapp.ui.activity

import com.kirakishou.fixmypc.fixmypcapp.base.BaseActivityFragmentCallback

/**
 * Created by kirakishou on 8/29/2017.
 */
interface ClientNewMalfunctionActivityFragmentCallback : BaseActivityFragmentCallback {
    fun requestPermission(permission: String, requestCode: Int)
    fun startActivity(activityClass: Class<*>, finishCurrentActivity: Boolean)
}