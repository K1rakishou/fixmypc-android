package com.kirakishou.fixmypc.fixmypcapp.helper.preference

import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.kirakishou.fixmypc.fixmypcapp.helper.util.extension.edit
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Constant
import com.kirakishou.fixmypc.fixmypcapp.mvp.model.Fickle
import timber.log.Timber


/**
 * Created by kirakishou on 9/7/2017.
 */
class MyCurrentLocationPreference(private val mSharedPreferences: SharedPreferences) : BasePreference {

    var mLocation: Fickle<LatLng> = Fickle.empty()

    private val mThisPrefPrefix = "MyCurrentLocationPreference"
    private val mLatitudeSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_latitude"
    private val mLongitudeSharedPrefKey = "${Constant.SHARED_PREFS_PREFIX}_${mThisPrefPrefix}_longitude"

    override fun save() {
        mSharedPreferences.edit {
            //sharedprefs can't save a double (why???) so we have to convert them and save them as strings
            it.putString(mLatitudeSharedPrefKey, mLocation.get().latitude.toString())
            it.putString(mLongitudeSharedPrefKey, mLocation.get().longitude.toString())
        }
    }

    override fun load() {
        val latitude = mSharedPreferences.getString(mLatitudeSharedPrefKey, null)
        val longitude = mSharedPreferences.getString(mLongitudeSharedPrefKey, null)

        if (latitude != null && longitude != null) {
            try {
                mLocation = Fickle.of(LatLng(latitude.toDouble(), longitude.toDouble()))
            } catch (e: NumberFormatException) {
                Timber.e("Couldn't convert saved latitude or longitude, {lat:$latitude, lon: $longitude}")
                clear()
            }
        }
    }

    override fun clear() {
        mSharedPreferences.edit {
            it.remove(mLatitudeSharedPrefKey)
            it.remove(mLongitudeSharedPrefKey)
        }
    }

    fun exists() = mLocation.isPresent()
}