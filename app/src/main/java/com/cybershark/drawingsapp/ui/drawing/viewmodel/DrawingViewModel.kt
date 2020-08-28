package com.cybershark.drawingsapp.ui.drawing.viewmodel

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.util.UIState
import kotlinx.coroutines.launch

class DrawingViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository,
    @Assisted private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().apply {
        value = UIState.IDLE
    }
    val uiState: LiveData<UIState> = _uiState

    val currentDrawing: LiveData<DrawingEntity> = mainRepository.getDrawingByID(stateHandle.get<Int>(INTENT_ID_KEY)!!)

    val listOfMarkers: LiveData<List<MarkerEntity>> = mainRepository.getMarkingsOfDrawingWith(stateHandle.get<Int>(INTENT_ID_KEY)!!)

    private val _listOfImagesRefreshState = MutableLiveData<Boolean>().apply { value = false }
    val listOfImagesRefreshState: LiveData<Boolean> = _listOfImagesRefreshState
    val listOfImages = mutableListOf<Uri>()
    lateinit var listOfImagesByMarkerID: LiveData<List<MarkerImagesEntity>>

    fun insertMarker(x: Float, y: Float, title: String, assignee: String, description: String, drawingID: Int, remarks: String) {
        _uiState.value = UIState.LOADING
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
            listOfImages.forEach {
                mainRepository.insertMarkerImage(
                    MarkerImagesEntity(
                        markerID = result.toInt(),
                        imageURI = it,
                        drawingID = drawingID
                    )
                )
            }
            if (result != -1L) {
                _uiState.value = UIState.COMPLETED("Added Marker!")
            } else {
                _uiState.value = UIState.ERROR("Error Adding Marker!")
            }
        }
    }

    fun updateMarker(newMarkerEntity: MarkerEntity) {
        _uiState.value = UIState.LOADING
        viewModelScope.launch {
            //update to marker table
            val result = mainRepository.updateMarker(newMarkerEntity)
            if (result != -1) {
                _uiState.value = UIState.COMPLETED("Updated Marker!")
            } else {
                _uiState.value = UIState.ERROR("Error Updating Marker!")
            }
        }
    }

    fun deleteMarker(markerID: Int) {
        TODO("delete marker by id from markers, delete images by id from marker_images, decrement marker count of drawing by one.")
    }

    fun insertMarkerImage(uri: Uri) = listOfImages.add(uri)

    fun getMarkerImagesByID(markerID: Int) {
        mainRepository.getMarkerImagesByID(markerID)
    }

    fun getMarkerByID(markerID: Int): MarkerEntity? {
        return listOfMarkers.value?.first { it.markerID == markerID }
    }

    companion object {
        const val TAG = "DrawingViewModel"
        const val INTENT_ID_KEY = "drawingID"
    }
}