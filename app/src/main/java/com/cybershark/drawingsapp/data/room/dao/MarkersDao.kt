package com.cybershark.drawingsapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.drawingsapp.data.room.entities.MarkerEntity
import com.cybershark.drawingsapp.data.room.entities.MarkersWithMarkerImages

@Dao
interface MarkersDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarker(markerEntity: MarkerEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateMarker(markerEntity: MarkerEntity): Int

    @Query("delete from markers where markerID=:markerID")
    suspend fun deleteMarkerByID(markerID: Int): Int

    @Query("delete from markers where drawingID=:drawingID")
    suspend fun deleteAllMarkersOfDrawingWithID(drawingID: Int)

    @Query("delete from markers")
    suspend fun deleteAddData()

    @Query("select count(*) from markers where drawingID=:drawingId")
    fun getMarkerCountOf(drawingId: Int): LiveData<Int>

    @Query("select * from markers")
    fun getAllMarkers(): LiveData<List<MarkerEntity>>

    @Query("select * from markers where drawingID=:id")
    fun getMarkersByID(id: Int): LiveData<List<MarkerEntity>>

    @Transaction
    @Query("select * from markers")
    fun getAllMarkersMerged(): LiveData<List<MarkersWithMarkerImages>>

    @Transaction
    @Query("select * from markers where drawingID=:id")
    fun getMarkersByIDMerged(id: Int): LiveData<List<MarkersWithMarkerImages>>
}
