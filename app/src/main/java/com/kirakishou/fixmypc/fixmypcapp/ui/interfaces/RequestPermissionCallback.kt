package com.kirakishou.fixmypc.fixmypcapp.ui.interfaces

/**
 * Created by kirakishou on 10/8/2017.
 */
interface RequestPermissionCallback {
    fun requestPermission(permission: String, requestCode: Int)
}