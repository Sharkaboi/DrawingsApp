package com.cybershark.drawingsapp.data.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DrawingsWithMarkersAndMarkerImages(
    @Embedded val drawingEntity: DrawingEntity,
    @Relation(
        entity = MarkerEntity::class,
        parentColumn = "id",
        entityColumn = "drawingID"
    )
    val markers: List<MarkersWithMarkerImages>
)

data class MarkersWithMarkerImages(
    @Embedded val markerEntity: MarkerEntity,
    @Relation(
        parentColumn = "markerID",
        entityColumn = "markerID"
    )
    val markerImages: List<MarkerImagesEntity>
)