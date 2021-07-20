package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.room.entities.DrawingEntity
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages

@Dao
interface DrawingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDrawing(drawingEntity: DrawingEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDrawing(drawingEntity: DrawingEntity): Int

    @Query("delete from drawings where id=:drawingID")
    suspend fun deletedDrawing(drawingID: Int): Int

    @Query("delete from drawings")
    suspend fun deleteAllData()

    @Query("select * from drawings order by timeAdded desc")
    fun getAllDrawings(): LiveData<List<DrawingEntity>>

    @Transaction
    @Query("select * from drawings order by timeAdded desc")
    fun getAllDrawingsMerged(): LiveData<List<DrawingsWithMarkersAndMarkerImages>>

    @Query("select * from drawings where id=:drawingID")
    fun getDrawingByID(drawingID: Int): DrawingEntity

    @Transaction
    @Query("select * from drawings where id=:drawingID")
    fun getDrawingByIDMerged(drawingID: Int): LiveData<DrawingsWithMarkersAndMarkerImages>
}
