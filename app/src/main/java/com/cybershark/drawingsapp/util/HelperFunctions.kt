package com.cybershark.drawingsapp.util

import android.content.Context
import android.widget.Toast
import java.util.*

internal fun Date.getFriendlyString(): String {
    val currentTime = Date()
    return when {
        currentTime.time - this.time <= 1800000L -> "A few minutes ago."
        currentTime.time - this.time <= 3600000L -> "An hour ago."
        currentTime.time - this.time in (86300000..86500000) -> "A day ago."
        currentTime.time - this.time in (604700000..604900000) -> "A week ago."
        currentTime.time - this.time in (2591000000..2593000000) -> "A month ago."
        currentTime.time - this.time in (31555952000..31557952000) -> "A year ago."
        else -> {
            val savedDateCalendar = Calendar.getInstance()
            savedDateCalendar.time = this
            return "Created at ${savedDateCalendar.getFormattedString()}"
        }
    }
}

internal fun Calendar.getFormattedString(): String {
    this.apply {
        return "${get(Calendar.HOUR)}:${get(Calendar.MINUTE)} ${get(Calendar.AM_PM)} " +
                "${get(Calendar.DAY_OF_MONTH)}/${get(Calendar.MONTH)}/${get(Calendar.YEAR)}"
    }
}

internal fun Context.shortToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
internal fun Context.longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()