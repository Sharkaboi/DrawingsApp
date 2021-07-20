package com.cybershark.drawingsapp.ui.add_edit_drawing.repository

import android.content.Context
import androidx.core.net.toFile
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.data.room.entities.DrawingEntity
import com.cybershark.drawingsapp.util.copyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AddEditRepository(
    private val drawingsDao: DrawingsDao,
    private val markersDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao,
    private val context: Context
) : IAddEditRepository {

    override suspend fun getDrawingById(drawingId: Int): Result<DrawingEntity> = withContext(Dispatchers.IO) {
        try {
            val drawing = drawingsDao.getDrawingByID(drawingId)
            return@withContext Result.success(drawing)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun updateDrawing(value: DrawingEntity?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (value == null) {
                return@withContext Result.failure(Throwable("An error occurred"))
            }
            val copiedImageUri = context.copyImage(value.imageURI)
            deleteMarkersAndImages(value)
            drawingsDao.updateDrawing(
                value.copy(imageURI = copiedImageUri)
            )
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun addDrawing(value: DrawingEntity?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (value == null) {
                return@withContext Result.failure(Throwable("An error occurred"))
            }
            val copiedImageUri = context.copyImage(value.imageURI)
            drawingsDao.insertDrawing(
                value.copy(imageURI = copiedImageUri)
            )
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    private suspend fun deleteMarkersAndImages(drawing: DrawingEntity) {
        markersDao.deleteAllMarkersOfDrawingWithID(drawing.id)
        markerImagesDao.deleteAllMarkerImagesWithDrawingID(drawing.id)
        drawingsDao.getDrawingByIDMerged(drawing.id).markers.forEach { marker ->
            marker.markerImages.forEach { markerImage ->
                markerImage.imageURI.toFile().delete()
            }
        }
    }
}