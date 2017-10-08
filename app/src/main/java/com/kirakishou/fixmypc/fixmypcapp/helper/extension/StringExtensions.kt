package com.kirakishou.fixmypc.fixmypcapp.helper.extension

/**
 * Created by kirakishou on 10/8/2017.
 */

fun String.removeSpaces(): String {
    val sb = StringBuilder(this.length)

    for (element in this) {
        if (!Character.isWhitespace(element)) {
            sb.append(element)
        }
    }

    return sb.toString()
}