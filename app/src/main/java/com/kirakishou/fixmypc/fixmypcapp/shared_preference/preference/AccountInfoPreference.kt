package com.kirakishou.fixmypc.fixmypcapp.shared_preference.preference

import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.extension.edit
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.Fickle
import com.kirakishou.fixmypc.fixmypcapp.shared_preference.BasePreference

/**
 * Created by kirakishou on 7/25/2017.
 */
class AccountInfoPreference(private val mSharedPreferences: SharedPreferences,
                            var login: Fickle<String>,
                            var password: Fickle<String>) : BasePreference {

    constructor(mSharedPreferences: SharedPreferences) : this(mSharedPreferences, Fickle.empty(), Fickle.empty())

    private val mThisPrefPrefix = "AccountInfoPreference"
    private val mLoginSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_login"
    private val mPasswordSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_password"

    override fun save() {
        mSharedPreferences.edit {
            it.putString(mLoginSharedPrefKey, login.get())
            it.putString(mPasswordSharedPrefKey, password.get())
        }
    }

    override fun load() {
        login = Fickle.of(mSharedPreferences.getString(mLoginSharedPrefKey, null))
        password = Fickle.of(mSharedPreferences.getString(mPasswordSharedPrefKey, null))
    }

    override fun clear() {
        mSharedPreferences.edit {
            it.remove(mLoginSharedPrefKey)
            it.remove(mPasswordSharedPrefKey)
        }
    }

    fun exists(): Boolean {
        return login.isPresent() && password.isPresent()
    }
}