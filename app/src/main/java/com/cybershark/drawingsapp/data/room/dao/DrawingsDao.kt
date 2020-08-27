package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.models.DrawingEntity

@Dao
interface DrawingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDrawing(drawingEntity: DrawingEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDrawing(drawingEntity: DrawingEntity): Long

    @Delete
    suspend fun deletedDrawing(drawingEntity: DrawingEntity): Long

    @Query("select * from drawings order by timeAdded desc")
    suspend fun getAllDrawings() : LiveData<List<DrawingEntity>>


}
