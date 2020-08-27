package com.cybershark.drawingsapp.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.data.room.typeconvertors.DateTypeConverter
import com.cybershark.drawingsapp.data.room.typeconvertors.UriTypeConverter

@Database(
    entities = [DrawingEntity::class, MarkerEntity::class, MarkerImagesEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(value = [DateTypeConverter::class, UriTypeConverter::class])
abstract class DrawingManagerDB : RoomDatabase() {

    abstract fun drawingsDao(): DrawingsDao

    abstract fun markersDao(): MarkersDao

    abstract fun markerImagesDao(): MarkerImagesDao


    companion object {
        const val dbName = "drawings_manager_db"
    }
}
