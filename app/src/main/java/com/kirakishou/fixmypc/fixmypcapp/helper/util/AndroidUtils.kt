package com.kirakishou.fixmypc.fixmypcapp.helper.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.Constant


/**
 * Created by kirakishou on 7/30/2017.
 */
object AndroidUtils {

    fun isAtleastLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun pixelsToSp(context: Context, px: Float): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun isLollipopOrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

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

    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        return size.x
    }

    fun calculateColumnsCount(context: Context, viewWidth: Int): Int {
        val screenWidth = getScreenWidth(context)
        val viewWidthDp = dpToPx(viewWidth.toFloat(), context).toInt()
        var columnsCount = Constant.RECYCLER_VIEW_MAX_COLUMNS_COUNT

        while (screenWidth / columnsCount < viewWidthDp) {
            --columnsCount

            if (columnsCount <= 1) {
                columnsCount = 1
                break
            }
        }

        return columnsCount
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (activity.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
        }
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
        val dp = convertDpToPixel(viewWidth.toFloat(), context).toInt()

        return when {
            screenSize / 8 >= dp -> 8
            screenSize / 7 >= dp -> 7
            screenSize / 6 >= dp -> 6
            screenSize / 5 >= dp -> 5
            screenSize / 4 >= dp -> 4
            screenSize / 3 >= dp -> 3
            screenSize / 2 >= dp -> 2
            else -> 1
        }

    }
}