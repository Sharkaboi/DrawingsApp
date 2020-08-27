package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity

@Dao
interface MarkerImagesDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarkerImage(markerImagesModel: MarkerImagesEntity): Long

    @Delete
    suspend fun deleteMarkerImage(markerImagesModel: MarkerImagesEntity): Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateMarkerImage(markerImagesModel: MarkerImagesEntity): Int

    @Query("select * from marker_images where markerID=:markerID")
    fun getMarkerImagesFromID(markerID: Int): LiveData<List<MarkerImagesEntity>>

}
