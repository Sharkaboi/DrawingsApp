package com.cybershark.drawingsapp.ui.drawing.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    // UI State live data to observe and show respective state
    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState

    // Drawing entity which is currently selected.
    val currentDrawing: LiveData<DrawingEntity> = mainRepository.getDrawingByID(stateHandle.get<Int>(INTENT_DRAWING_ID_KEY)!!)

    // List of markers of current drawing from room
    val listOfMarkers: LiveData<List<MarkerEntity>> = mainRepository.getMarkingsOfDrawingWith(stateHandle.get<Int>(INTENT_DRAWING_ID_KEY)!!)

    // Temporary list for image attachments
    private val _listOfImages = MutableLiveData<List<Uri>>(emptyList())
    val listOfImages: LiveData<List<Uri>> = _listOfImages

    // List of stored attached images for all markers from room.
    val listOfMarkerImagesFromRoom: LiveData<List<MarkerImagesEntity>> = mainRepository.getAllMarkerImages()

    // Inserts marker to marker table, increments marker count in drawing table and inserts any attachments to marker_images table.
    fun insertMarker(x: Float, y: Float, title: String, assignee: String, description: String, drawingID: Int, remarks: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            //insert to marker table
            val result = mainRepository.insertMarker(
                MarkerEntity(
                    markerPositionX = x,
                    markerPositionY = y,
                    title = title,
                    assignee = assignee,
                    description = description,
                    drawingID = drawingID,
                    remarks = remarks
                )
            )
            // increment marker in drawing table
            mainRepository.incrementMarkerCount(drawingID)
            // insert marker images into marker images table
            mainRepository.insertMarkerImage(result.toInt(), drawingID, _listOfImages.value ?: emptyList())
            if (result != -1L) {
                _uiState.setSuccess("Added Marker!")
            } else {
                _uiState.setError("Error Adding Marker!")
            }
        }
    }

    // Updates marker with new details.
    fun updateMarker(newMarkerEntity: MarkerEntity) {
        Log.d(TAG, "updateMarker: $newMarkerEntity")
        _uiState.setLoading()
        viewModelScope.launch {
            //update to marker table
            val result = mainRepository.updateMarker(newMarkerEntity)
            mainRepository.insertMarkerImage(newMarkerEntity.markerID, newMarkerEntity.drawingID, _listOfImages.value ?: emptyList())
            if (result != -1) {
                _uiState.setSuccess("Updated Marker!")
            } else {
                _uiState.setError("Error Updating Marker!")
            }
        }
    }

    // Deletes marker from marker table, deletes all attached images, decrements marker count in drawing table by 1.
    fun deleteMarker(markerID: Int, drawingID: Int) {
        _uiState.setLoading()
        viewModelScope.launch {
            //delete to marker table
            val result = mainRepository.deleteMarkerByID(markerID)
            mainRepository.deleteAllMarkerImagesWithMarkerID(markerID)
            mainRepository.decrementMarkerCount(drawingID)
            if (result != -1) {
                _uiState.setSuccess("Deleted Marker!")
            } else {
                _uiState.setError("Error Deleting Marker!")
            }
        }
    }

    // Insert Image into temporary list of attachments live data wrapper.
    fun insertMarkerImage(uri: Uri) {
        _listOfImages.value = _listOfImages.value?.plus(uri)
    }

    companion object {
        const val TAG = "DrawingViewModel"
        const val INTENT_DRAWING_ID_KEY = "drawingID"
    }
}