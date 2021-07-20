package com.cybershark.drawingsapp.ui.drawing.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.data.room.entities.MarkerEntity
import com.cybershark.drawingsapp.data.room.entities.MarkerImagesEntity
import com.cybershark.drawingsapp.util.copyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrawingRepository(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao,
    private val context: Context
) : IDrawingRepository {

    override suspend fun insertMarker(markerEntity: MarkerEntity, listOfImages: List<Uri>) = withContext(Dispatchers.IO) {
        try {
            val id = markerDao.insertMarker(markerEntity)
            insertMarkerImages(markerID = id.toInt(), listOfImages = listOfImages)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun updateMarker(newMarkerEntity: MarkerEntity, listOfImages: List<Uri>) = withContext(Dispatchers.IO) {
        try {
            deleteMarkerImages(newMarkerEntity.markerID)
            markerDao.updateMarker(newMarkerEntity)
            insertMarkerImages(markerID = newMarkerEntity.markerID, listOfImages = listOfImages)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun deleteMarkerByID(markerID: Int) = withContext(Dispatchers.IO) {
        try {
            deleteMarkerImages(markerID)
            markerDao.deleteMarkerByID(markerID)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override fun getDrawingById(drawingId: Int): LiveData<DrawingsWithMarkersAndMarkerImages> {
        return drawingsDao.getDrawingByIDMerged(drawingId)
    }

    private suspend fun deleteMarkerImages(markerID: Int) {
        markerImagesDao.deleteAllMarkerImagesWithMarkerID(markerID)
        markerImagesDao.getMarkerImagesFromID(markerID).forEach {
            if (!it.imageURI.toFile().delete()) {
                throw Exception("Images couldn't be deleted")
            }
        }
    }

    private suspend fun insertMarkerImages(markerID: Int, listOfImages: List<Uri>) = withContext(Dispatchers.IO) {
        listOfImages.forEach { imageUri ->
            val copiedUri: Uri = context.copyImage(imageUri)
            markerImagesDao.insertMarkerImage(
                MarkerImagesEntity(markerID = markerID, imageURI = copiedUri)
            )
        }
    }

}