package com.cybershark.drawingsapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cybershark.drawingsapp.data.models.MarkerEntity

@Dao
interface MarkersDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarker(markerEntity: MarkerEntity): Long
}
