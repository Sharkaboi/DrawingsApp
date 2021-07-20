package com.cybershark.drawingsapp.ui.main.repository

import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao
) : IMainRepository {

    override val drawingsList: LiveData<List<DrawingsWithMarkersAndMarkerImages>> = drawingsDao.getAllDrawingsMerged()

    override suspend fun deleteDrawingAndAllMarkers(drawing: DrawingsWithMarkersAndMarkerImages): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            deleteSavedImagesOfDrawing(drawing)
            drawingsDao.deletedDrawing(drawing.drawingEntity.id)
            markerDao.deleteAllMarkersOfDrawingWithID(drawing.drawingEntity.id)
            markerImagesDao.deleteAllMarkerImagesWithDrawingID(drawing.drawingEntity.id)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    private fun deleteSavedImagesOfDrawing(drawing: DrawingsWithMarkersAndMarkerImages) {
        drawing.drawingEntity.imageURI.toFile().delete()
        drawing.markers.forEach { marker ->
            marker.markerImages.forEach { markerImage ->
                markerImage.imageURI.toFile().delete()
            }
        }
    }
}
