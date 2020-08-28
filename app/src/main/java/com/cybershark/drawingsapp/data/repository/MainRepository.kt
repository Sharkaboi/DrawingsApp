package com.cybershark.drawingsapp.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.room.dao.DrawingsDao
import com.cybershark.drawingsapp.data.room.dao.MarkerImagesDao
import com.cybershark.drawingsapp.data.room.dao.MarkersDao
import com.cybershark.drawingsapp.ui.main.AddOrEditDrawingDialog
import com.cybershark.drawingsapp.util.longToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.channels.FileChannel

class MainRepository
constructor(
    private val drawingsDao: DrawingsDao,
    private val markerDao: MarkersDao,
    private val markerImagesDao: MarkerImagesDao,
    private val context: Context
) {

    // Drawings dao operations
    val drawingsList: LiveData<List<DrawingEntity>> = drawingsDao.getAllDrawings()

    suspend fun insertDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.insertDrawing(drawingEntity)
    }

    suspend fun updateDrawing(drawingEntity: DrawingEntity) = withContext(Dispatchers.IO) {
        drawingsDao.updateDrawing(drawingEntity)
    }

    suspend fun deleteDrawing(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.deletedDrawing(drawingID)
    }

    fun getDrawingByID(drawingID: Int): LiveData<DrawingEntity> {
        return drawingsDao.getDrawingByID(drawingID)
    }

    suspend fun incrementMarkerCount(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.incrementMarkerCount(drawingID)
    }

    suspend fun decrementMarkerCount(drawingID: Int) = withContext(Dispatchers.IO) {
        drawingsDao.decrementMarkerCount(drawingID)
    }

    // markers dao operations
    suspend fun insertMarker(markerEntity: MarkerEntity) = withContext(Dispatchers.IO) {
        markerDao.insertMarker(markerEntity)
    }

    fun getMarkingsOfDrawingWith(drawingID: Int): LiveData<List<MarkerEntity>> {
        return markerDao.getMarkersByID(drawingID)
    }

    suspend fun updateMarker(newMarkerEntity: MarkerEntity) = withContext(Dispatchers.IO) {
        markerDao.updateMarker(newMarkerEntity)
    }

    suspend fun deleteAllMarkersOfDrawingWithID(drawingID: Int) = withContext(Dispatchers.IO) {
        markerDao.deleteAllMarkersOfDrawingWithID(drawingID)
    }

    suspend fun deleteMarkerByID(markerID: Int) = withContext(Dispatchers.IO) {
        markerDao.deleteMarkerByID(markerID)
    }

    // marker images dao operations
    suspend fun insertMarkerImage(markerID: Int, drawingID: Int, listOfImages: List<Uri>) = withContext(Dispatchers.IO) {
        listOfImages.forEach { imageUri ->
            val copiedUri: Uri? = getCopiedUriFromFile(imageUri)
            if (copiedUri != null) {
                markerImagesDao.insertMarkerImage(
                    MarkerImagesEntity(markerID = markerID, drawingID = drawingID, imageURI = copiedUri)
                )
            }
        }
    }

    private fun getCopiedUriFromFile(imageUri: Uri): Uri? {
        // Path to copy file to.
        val destinationFolder = File("${context.getExternalFilesDir(null)}${File.separator}Drawings${File.separator}")
        val inputFile = imageUri.toFile()
        try {
            val exportFile = File("${destinationFolder.path}${File.separator}${inputFile.name}")

            if (!destinationFolder.exists()) {
                destinationFolder.mkdir()
            }
            if (destinationFolder.canWrite()) {
                var inputChannel: FileChannel? = null
                var outputChannel: FileChannel? = null
                try {
                    inputChannel = inputFile.inputStream().channel
                    outputChannel = exportFile.outputStream().channel
                } catch (e: Exception) {
                    Log.d(AddOrEditDrawingDialog.TAG, "copyImage: ${e.message}")
                }
                inputChannel?.transferTo(0, inputChannel.size(), outputChannel)
                inputChannel?.close()
                outputChannel?.close()

                // Inserts new image uri after copying to temporary list in viewmodel.
                return exportFile.toUri()
            } else {
                Log.e(AddOrEditDrawingDialog.TAG, "copyImage: No write perm")
            }
        } catch (e: Exception) {
            context.longToast("Error copying image!")
            Log.e(AddOrEditDrawingDialog.TAG, "copyImage: ${e.printStackTrace()}")
        }
        return null
    }

    fun getAllMarkerImages(): LiveData<List<MarkerImagesEntity>> {
        return markerImagesDao.getAllMarkerImages()
    }

    fun getMarkerImagesByID(markerID: Int): LiveData<List<MarkerImagesEntity>> {
        return markerImagesDao.getMarkerImagesFromID(markerID)
    }

    suspend fun deleteAllMarkerImagesWithDrawingID(drawingID: Int) = withContext(Dispatchers.IO) {
        markerImagesDao.deleteAllMarkerImagesWithDrawingID(drawingID)
    }

    suspend fun deleteAllMarkerImagesWithMarkerID(markerID: Int) = withContext(Dispatchers.IO) {
        markerImagesDao.deleteAllMarkerImagesWithMarkerID(markerID)
    }

    // Deletes all data from tables and external dir
    suspend fun deleteAddData() = withContext(Dispatchers.IO) {
        markerDao.deleteAddData()
        drawingsDao.deleteAllData()
        deleteAllImagesFromAppFolder()
        markerImagesDao.deleteAllData()
    }

    private fun deleteAllImagesFromAppFolder() {
        try {
            val dir = File("${context.getExternalFilesDir(null)}${File.separator}Drawings")
            val result = dir.deleteRecursively()
            Log.d(TAG, "deleteAllImagesFromAppFolder: $result")
        } catch (e: Exception) {
            Log.e(TAG, "deleteAllImagesFromAppFolder: ${e.printStackTrace()}")
        }
    }

    companion object {
        const val TAG = "MainRepository"
    }
}
