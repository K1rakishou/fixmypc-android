package com.kirakishou.fixmypc.fixmypcapp.util

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager


/**
 * Created by kirakishou on 7/30/2017.
 */
object AndroidUtils {

    fun dpToPx(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return px
    }

    fun pxToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return dp
    }

    fun getScreenSize(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        return size.x
    }

    fun calculateNoOfColumns(context: Context, viewWidth: Int): Int {
        val screenSize = getScreenSize(context)
        val dp = dpToPx(viewWidth.toFloat(), context).toInt()

        if (screenSize / 4 >= dp) {
            return 4
        } else if (screenSize / 3 >= dp) {
            return 3
        } else if (screenSize / 2 >= dp) {
            return 2
        }

        return 1
    }
}