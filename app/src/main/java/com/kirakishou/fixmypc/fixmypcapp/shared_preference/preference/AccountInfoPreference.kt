package com.kirakishou.fixmypc.fixmypcapp.shared_preference.preference

import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.extension.edit
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.BasePreference

/**
 * Created by kirakishou on 7/25/2017.
 */
class AccountInfoPreference(private val mSharedPreferences: SharedPreferences,
                            var mLogin: Fickle<String>,
                            var mPassword: Fickle<String>) : BasePreference {

    constructor(mSharedPreferences: SharedPreferences) : this(mSharedPreferences, Fickle.empty(), Fickle.empty())

    private val mThisPrefPrefix = "AccountInfoPreference"
    private val mLoginSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_login"
    private val mPasswordSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_password"

    override fun save() {
        mSharedPreferences.edit {
            it.putString(mLoginSharedPrefKey, mLogin.get())
            it.putString(mPasswordSharedPrefKey, mPassword.get())
        }
    }

    override fun load() {
        this.mLogin = Fickle.of(mSharedPreferences.getString(mLoginSharedPrefKey, null))
        this.mPassword = Fickle.of(mSharedPreferences.getString(mPasswordSharedPrefKey, null))
    }

    override fun clear() {
        mSharedPreferences.edit {
            it.remove(mLoginSharedPrefKey)
            it.remove(mPasswordSharedPrefKey)
        }
    }
}