package com.cybershark.drawingsapp.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker_images")
data class MarkerImagesEntity(
    @PrimaryKey(autoGenerate = true)
    val imageID: Int = 0,
    val markerID: Int,
    val imageURI: Uri
)