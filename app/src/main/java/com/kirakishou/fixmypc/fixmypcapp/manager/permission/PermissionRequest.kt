package com.kirakishou.fixmypc.fixmypcapp.manager.permission

/**
 * Created by kirakishou on 7/31/2017.
 */
class PermissionRequest(val requestCode: Int, val permissions: Array<out String>, val callback: (granted: Boolean) -> Unit)