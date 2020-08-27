package com.cybershark.drawingsapp.data.repository

import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository
constructor(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao
) {

    suspend fun insertDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO){
        drawingsDao.insertDrawing(drawingEntity)
    }

    suspend fun updateDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO){
        drawingsDao.updateDrawing(drawingEntity)
    }

    suspend fun getAllDrawings(): LiveData<List<DrawingEntity>> = withContext(Dispatchers.IO){
        drawingsDao.getAllDrawings()
    }

    suspend fun deleteDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.deletedDrawing(drawingEntity)
    }

}
