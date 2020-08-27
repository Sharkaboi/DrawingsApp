package com.cybershark.drawingsapp.data.repository

import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository
constructor(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao
) {

    val drawingsList: LiveData<List<DrawingEntity>> = drawingsDao.getAllDrawings()

    suspend fun insertDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.insertDrawing(drawingEntity)
    }

    suspend fun updateDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.updateDrawing(drawingEntity)
    }


    suspend fun deleteDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.deletedDrawing(drawingEntity)
    }

    fun getDrawingByID(drawingID: Int): LiveData<DrawingEntity> {
        return drawingsDao.getDrawingByID(drawingID)
    }

    suspend fun incrementMarkerCount(drawingID: Int) = withContext(Dispatchers.IO){
        drawingsDao.incrementMarkerCount(drawingID)
    }

    suspend fun insertMarker(markerEntity: MarkerEntity) = withContext(Dispatchers.IO) {
        markerDao.insertMarker(markerEntity)
    }

    suspend fun insertMarkerImage(markerImagesEntity: MarkerImagesEntity) = withContext(Dispatchers.IO){
        markerImagesDao.insertMarkerImage(markerImagesEntity)
    }

    fun getMarkingsOfDrawingWith(drawingID: Int): LiveData<List<MarkerEntity>> {
        return markerDao.getMarkersByID(drawingID)
    }
    companion object {
        const val TAG = "MainRepository"
    }
}
