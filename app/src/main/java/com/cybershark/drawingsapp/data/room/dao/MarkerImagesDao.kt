package com.cybershark.drawingsapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity

@Dao
interface MarkerImagesDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarkerImage(markerImagesModel: MarkerImagesEntity): Long
}
