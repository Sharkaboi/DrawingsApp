package com.cybershark.drawingsapp.ui.drawing.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.data.room.entities.MarkerEntity
import com.cybershark.drawingsapp.ui.drawing.repository.DrawingRepository
import com.cybershark.drawingsapp.ui.drawing.ui.DrawingDetailedActivity
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel
@Inject
constructor(
    private val drawingRepository: DrawingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State live data to observe and show respective state
    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState
    val currentDrawing: LiveData<DrawingsWithMarkersAndMarkerImages> =
        drawingRepository.getDrawingById(savedStateHandle.get<Int>(DrawingDetailedActivity.INTENT_ID_KEY) ?: 0)

    // Temporary list for image attachments
    private val _listOfImages = MutableLiveData<List<Uri>>(emptyList())
    val listOfImages: LiveData<List<Uri>> = _listOfImages

    // Inserts marker to marker table,
    // inserts any attachments to marker_images table.
    fun insertMarker(markerEntity: MarkerEntity) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = drawingRepository.insertMarker(
//                MarkerEntity(
//                    markerPositionX = x,
//                    markerPositionY = y,
//                    title = title,
//                    assignee = assignee,
//                    description = description,
//                    drawingID = drawingID,
//                    remarks = remarks
//                )
                markerEntity, _listOfImages.value ?: emptyList()
            )
            if (result.isSuccess) {
                resetTempImagesList()
                _uiState.setSuccess("Added Marker!")
            } else {
                _uiState.setError("Error Adding Marker!")
            }
        }
    }

    // Updates marker with new details.
    fun updateMarker(newMarkerEntity: MarkerEntity) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = drawingRepository.updateMarker(newMarkerEntity, _listOfImages.value ?: emptyList())
            if (result.isSuccess) {
                resetTempImagesList()
                _uiState.setSuccess("Updated Marker!")
            } else {
                _uiState.setError("Error Updating Marker!")
            }
        }
    }

    // Deletes marker from marker table,
    // deletes all attached images.
    fun deleteMarker(markerID: Int) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = drawingRepository.deleteMarkerByID(markerID)
            if (result.isSuccess) {
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

    private fun resetTempImagesList() {
        _listOfImages.value = emptyList()
    }
}