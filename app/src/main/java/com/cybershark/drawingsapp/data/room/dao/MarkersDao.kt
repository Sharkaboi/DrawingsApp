package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.models.MarkerEntity

@Dao
interface MarkersDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarker(markerEntity: MarkerEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateMarker(markerEntity: MarkerEntity): Long

    @Delete
    suspend fun deleteMarker(markerEntity: MarkerEntity): Long

    @Query("select count(*) from markers where drawingID=:drawingId")
    suspend fun getMarkerCountOf(drawingId: Int): LiveData<Int>

    @Query("select * from markers")
    suspend fun getAllMarkers(): LiveData<List<MarkerEntity>>

}
