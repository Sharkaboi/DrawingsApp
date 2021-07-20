package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.room.entities.MarkerImagesEntity

@Dao
interface MarkerImagesDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarkerImage(markerImagesModel: MarkerImagesEntity): Long

    @Delete
    suspend fun deleteMarkerImage(markerImagesModel: MarkerImagesEntity): Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateMarkerImage(markerImagesModel: MarkerImagesEntity): Int

    @Query("delete from marker_images where drawingID=:drawingID")
    suspend fun deleteAllMarkerImagesWithDrawingID(drawingID: Int)

    @Query("delete from marker_images")
    suspend fun deleteAllData()

    @Query("delete from marker_images where markerID=:markerID")
    suspend fun deleteAllMarkerImagesWithMarkerID(markerID: Int) : Int

    @Query("select * from marker_images where markerID=:markerID")
    suspend fun getMarkerImagesFromID(markerID: Int): List<MarkerImagesEntity>

    @Query("select * from marker_images")
    fun getAllMarkerImages(): LiveData<List<MarkerImagesEntity>>
}
