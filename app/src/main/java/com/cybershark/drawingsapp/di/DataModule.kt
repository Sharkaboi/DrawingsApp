package com.cybershark.drawingsapp.di

import android.content.Context
import androidx.room.Room
import com.cybershark.drawingsapp.ui.main.repository.IMainRepository
import com.cybershark.drawingsapp.ui.main.repository.MainRepository
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.data.room.db.DrawingManagerDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideMainRepository(
        drawingsDao: DrawingsDao,
        markersDao: MarkersDao,
        markerImagesDao: MarkerImagesDao
    ): IMainRepository = MainRepository(drawingsDao, markersDao, markerImagesDao)

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