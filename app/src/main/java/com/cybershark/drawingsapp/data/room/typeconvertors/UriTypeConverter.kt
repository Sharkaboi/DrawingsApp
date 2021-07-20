package com.cybershark.drawingsapp.data.room.typeconvertors

import android.net.Uri
import androidx.room.TypeConverter

class UriTypeConverter {

    @TypeConverter
    fun uriFromString(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

}