package com.cybershark.drawingsapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val markerID: Int = 0,
    val drawingID: Int,
    val title: String,
    val description: String,
    val remarks: String,
    val assignee: String,
    val markerPositionX: Long,
    val markerPositionY: Long
)