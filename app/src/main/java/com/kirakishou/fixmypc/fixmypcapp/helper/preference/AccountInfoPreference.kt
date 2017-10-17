package com.kirakishou.fixmypc.fixmypcapp.helper.preference

import android.content.SharedPreferences
import com.kirakishou.fixmypc.fixmypcapp.helper.extension.edit
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Fickle
import timber.log.Timber

/**
 * Created by kirakishou on 7/25/2017.
 */
class AccountInfoPreference(private val mSharedPreferences: SharedPreferences) : BasePreference {

    var login: Fickle<String> = Fickle.empty()
    var password: Fickle<String> = Fickle.empty()

    private val mThisPrefPrefix = "AccountInfoPreference"
    private val mLoginSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_login"
    private val mPasswordSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_password"

    override fun save() {
        mSharedPreferences.edit {
            if (login.isPresent() && password.isPresent()) {
                it.putString(mLoginSharedPrefKey, login.get())
                it.putString(mPasswordSharedPrefKey, password.get())
            }

            it.commit()
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

            login = Fickle.empty()
            password = Fickle.empty()
        }
    }

    fun exists() = login.isPresent() && password.isPresent()
}