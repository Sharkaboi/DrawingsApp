package com.cybershark.drawingsapp.data.room.typeconvertors

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter
    fun toLong(date: Date) = date.time

    @TypeConverter
    fun toDate(dateInLong: Long) = Date(dateInLong)

}