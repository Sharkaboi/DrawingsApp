package com.cybershark.drawingsapp.ui.main.repository

import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages

interface IMainRepository {
    val drawingsList: LiveData<List<DrawingsWithMarkersAndMarkerImages>>

    suspend fun deleteDrawingAndAllMarkers(drawing: DrawingsWithMarkersAndMarkerImages): Result<Unit>
}
