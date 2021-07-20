package com.cybershark.drawingsapp.ui.settings.repository

import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SettingsRepository(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao
) : ISettingsRepository {

    override suspend fun deleteAddData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            deleteAllImagesFromAppFolder()
            markerDao.deleteAddData()
            drawingsDao.deleteAllData()
            markerImagesDao.deleteAllData()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    private fun deleteAllImagesFromAppFolder() {
        // FIXME: 19-07-2021 refactor after insert logic
//        val dir = File("${context.getExternalFilesDir(null)}${File.separator}Drawings")
//        val result = dir.deleteRecursively()
    }
}