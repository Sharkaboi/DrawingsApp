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

    @Query("select * from drawings order by timeAdded desc")
    fun getAllDrawings() : LiveData<List<DrawingEntity>>

}
