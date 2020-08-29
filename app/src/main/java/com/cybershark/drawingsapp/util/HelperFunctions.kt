package com.cybershark.drawingsapp.util

import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import java.util.*


internal fun Date.getFriendlyString(): String {
    val currentTime = Date()
    val timeInAgo = DateUtils.getRelativeTimeSpanString(this.time, currentTime.time, DateUtils.MINUTE_IN_MILLIS)
    return timeInAgo.toString()
}

internal fun Context.shortToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
internal fun Context.longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
