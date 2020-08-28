package com.cybershark.drawingsapp.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainRepository
constructor(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao,
    private val context: Context
) {

    // Drawings dao operations
    val drawingsList: LiveData<List<DrawingEntity>> = drawingsDao.getAllDrawings()

    suspend fun insertDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.insertDrawing(drawingEntity)
    }

    suspend fun updateDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.updateDrawing(drawingEntity)
    }

    suspend fun deleteDrawing(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.deletedDrawing(drawingID)
    }

    fun getDrawingByID(drawingID: Int): LiveData<DrawingEntity> {
        return drawingsDao.getDrawingByID(drawingID)
    }

    suspend fun incrementMarkerCount(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.incrementMarkerCount(drawingID)
    }

    suspend fun decrementMarkerCount(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.decrementMarkerCount(drawingID)
    }

    // markers dao operations
    suspend fun insertMarker(markerEntity: MarkerEntity) = withContext(Dispatchers.IO) {
        markerDao.insertMarker(markerEntity)
    }

    fun getMarkingsOfDrawingWith(drawingID: Int): LiveData<List<MarkerEntity>> {
        return markerDao.getMarkersByID(drawingID)
    }

    suspend fun updateMarker(newMarkerEntity: MarkerEntity) = withContext(Dispatchers.IO) {
        markerDao.updateMarker(newMarkerEntity)
    }

    suspend fun deleteAllMarkersOfDrawingWithID(drawingID: Int) = withContext(Dispatchers.IO) {
        markerDao.deleteAllMarkersOfDrawingWithID(drawingID)
    }

    // marker images dao operations
    suspend fun insertMarkerImage(markerImagesEntity: MarkerImagesEntity) = withContext(Dispatchers.IO) {
        markerImagesDao.insertMarkerImage(markerImagesEntity)
    }

    fun getMarkerImagesByID(markerID: Int): LiveData<List<MarkerImagesEntity>> {
        return markerImagesDao.getMarkerImagesFromID(markerID)
    }

    suspend fun deleteAllMarkerImagesWithDrawingID(drawingID: Int) = withContext(Dispatchers.IO) {
        markerImagesDao.deleteAllMarkerImagesWithDrawingID(drawingID)
    }

    // Deletes all data from tables and external dir
    suspend fun deleteAddData() = withContext(Dispatchers.IO) {
        markerDao.deleteAddData()
        drawingsDao.deleteAllData()
        deleteAllImagesFromAppFolder()
        markerImagesDao.deleteAllData()
    }

    private fun deleteAllImagesFromAppFolder() {
        try {
            val dir = File("${context.getExternalFilesDir(null)}${File.separator}Drawings")
            val result = dir.deleteRecursively()
            Log.d(TAG, "deleteAllImagesFromAppFolder: $result")
        } catch (e: Exception) {
            Log.e(TAG, "deleteAllImagesFromAppFolder: ${e.printStackTrace()}")
        }
    }

    companion object {
        const val TAG = "MainRepository"
    }
}
