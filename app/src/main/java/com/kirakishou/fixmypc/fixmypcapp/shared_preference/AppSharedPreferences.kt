package com.kirakishou.fixmypc.fixmypcapp.shared_preference

import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.preference.AccountInfoPreference
import javax.inject.Inject

/**
 * Created by kirakishou on 7/25/2017.
 */
class AppSharedPreferences
    @Inject constructor(val mSharedPreferences: SharedPreferences) {

    enum class SharedPreferenceType {
        AccountInfo
    }

    fun <T : BasePreference> get(type: SharedPreferenceType): T {
        when (type) {
            SharedPreferenceType.AccountInfo -> return AccountInfoPreference(mSharedPreferences) as T
        }
    }
}