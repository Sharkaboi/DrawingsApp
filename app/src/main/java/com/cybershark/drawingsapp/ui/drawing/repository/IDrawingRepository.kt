package com.cybershark.drawingsapp.ui.drawing.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.data.room.entities.MarkerEntity

interface IDrawingRepository {
    fun getDrawingById(drawingId: Int): LiveData<DrawingsWithMarkersAndMarkerImages>
    suspend fun insertMarker(markerEntity: MarkerEntity, listOfImages: List<Uri>): Result<Unit>
    suspend fun deleteMarkerByID(markerID: Int): Result<Unit>
    suspend fun updateMarker(newMarkerEntity: MarkerEntity, listOfImages: List<Uri>): Result<Unit>
}