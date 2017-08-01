package com.kirakishou.fixmypc.fixmypcapp.module.shared_preference

import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.module.shared_preference.preference.AccountInfoPreference
import javax.inject.Inject

/**
 * Created by kirakishou on 7/25/2017.
 */
class AppSharedPreferences
    @Inject constructor(protected val mSharedPreferences: SharedPreferences) {

    inline fun <reified T : BasePreference> prepare(): T {
        return accessPrepare(T::class.java)
    }

    @PublishedApi
    internal fun <T : BasePreference> accessPrepare(clazz: Class<*>): T {
        when (clazz) {
            AccountInfoPreference::class.java -> return AccountInfoPreference(mSharedPreferences) as T
            else -> throw IllegalArgumentException("Unknown type T: ${clazz.simpleName}")
        }
    }
}