package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.models.DrawingEntity

@Dao
interface DrawingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDrawing(drawingEntity: DrawingEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDrawing(drawingEntity: DrawingEntity): Int

    @Delete
    suspend fun deletedDrawing(drawingEntity: DrawingEntity): Int

    @Query("update drawings set markerCount=markerCount+1 where id=:drawingID")
    suspend fun incrementMarkerCount(drawingID: Int)

    @Query("select * from drawings order by timeAdded desc")
    fun getAllDrawings(): LiveData<List<DrawingEntity>>

    @Query("select * from drawings where id=:drawingID")
    fun getDrawingByID(drawingID: Int): LiveData<DrawingEntity>

}
