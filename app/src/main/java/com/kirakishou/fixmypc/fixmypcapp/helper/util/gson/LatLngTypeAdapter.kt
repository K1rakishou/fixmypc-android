package com.kirakishou.fixmypc.fixmypcapp.helper.util.gson

import com.google.android.gms.maps.model.LatLng
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant

/**
 * Created by kirakishou on 8/29/2017.
 */
class LatLngTypeAdapter : TypeAdapter<LatLng>() {

    override fun read(input: JsonReader): LatLng {
        val lon = input.nextDouble()
        val lat = input.nextDouble()

        return LatLng(lat, lon)
    }

    override fun write(output: JsonWriter, location: LatLng) {
        output.beginObject()
        output.jsonValue(Constant.SerializedNames.LOCATION_LON)!!.value(location.longitude)
        output.jsonValue(Constant.SerializedNames.LOCATION_LAT)!!.value(location.latitude)
        output.endObject()
    }
}