package com.cybershark.drawingsapp.di

import android.content.Context
import androidx.room.Room
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.data.room.db.DrawingManagerDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideRepository(
        drawingsDao: DrawingsDao,
        markersDao: MarkersDao,
        markerImagesDao: MarkerImagesDao
    ): MainRepository = MainRepository(drawingsDao, markersDao, markerImagesDao)

    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext context: Context): DrawingManagerDB {
        return Room.databaseBuilder(context, DrawingManagerDB::class.java, DrawingManagerDB.dbName)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDrawingsDao(drawingManagerDB: DrawingManagerDB): DrawingsDao =
        drawingManagerDB.drawingsDao()

    @Provides
    @Singleton
    fun provideMarkersDao(drawingManagerDB: DrawingManagerDB): MarkersDao =
        drawingManagerDB.markersDao()

    @Provides
    @Singleton
    fun provideMarkersImagesDao(drawingManagerDB: DrawingManagerDB): MarkerImagesDao =
        drawingManagerDB.markerImagesDao()

}