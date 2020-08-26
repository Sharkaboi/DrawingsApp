package com.cybershark.drawingsapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cybershark.drawingsapp.data.models.DrawingEntity

@Dao
interface DrawingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDrawing(drawingEntity: DrawingEntity): Long
}
