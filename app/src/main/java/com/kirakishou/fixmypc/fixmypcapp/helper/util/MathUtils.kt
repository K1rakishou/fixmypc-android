package com.kirakishou.fixmypc.fixmypcapp.helper.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by kirakishou on 9/7/2017.
 */
object MathUtils {
    fun round(value: Double, places: Int): Double {
        if (places < 0) throw IllegalArgumentException()

        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371
        val dLat = Math.abs(Math.toRadians(lat2 - lat1))
        val dLon = Math.abs(Math.toRadians(lon2 - lon1))
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}