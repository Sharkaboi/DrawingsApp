package com.cybershark.drawingsapp.ui.main.viewmodel

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.util.UIState
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository,
    @Assisted stateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().apply {
        value = UIState.IDLE
    }
    val uiState: LiveData<UIState> = _uiState
    private val _drawingsList = mainRepository.drawingsList
    val drawingsList: LiveData<List<DrawingEntity>> = _drawingsList

    fun getDrawingByID(drawingID: Int): DrawingEntity {
        return _drawingsList.value!!.first { it.id == drawingID }
    }

    fun updateDrawing(drawingEntity: DrawingEntity) {
        _uiState.value = UIState.LOADING
        viewModelScope.launch {
            val previousDrawingEntity = _drawingsList.value?.first { it.id == drawingEntity.id }
            // Deleting all markers and marker images of drawing if image is updated.
            if (previousDrawingEntity?.imageURI != drawingEntity.imageURI) {
                mainRepository.deleteAllMarkerImagesWithDrawingID(drawingEntity.id)
                mainRepository.deleteAllMarkersOfDrawingWithID(drawingEntity.id)
                val newEntityWithZeroMarkerCount: DrawingEntity = drawingEntity.copy(markerCount = 0)
                val result = mainRepository.updateDrawing(newEntityWithZeroMarkerCount)
                if (result == -1) {
                    _uiState.value = UIState.ERROR("Updation failed!")
                } else {
                    _uiState.value = UIState.COMPLETED("Updation successful!")
                }
            } else {
                val result = mainRepository.updateDrawing(drawingEntity)
                if (result == -1) {
                    _uiState.value = UIState.ERROR("Updation failed!")
                } else {
                    _uiState.value = UIState.COMPLETED("Updation successful!")
                }
            }
        }
    }

    fun insertDrawing(title: String, uri: Uri, date: Date) {
        _uiState.value = UIState.LOADING
        val drawingItem = DrawingEntity(
            title = title,
            imageURI = uri,
            timeAdded = date,
            markerCount = 0
        )
        viewModelScope.launch {
            val result = mainRepository.insertDrawing(drawingItem)
            if (result == -1L) {
                _uiState.value = UIState.ERROR("Insertion failed!")
            } else {
                _uiState.value = UIState.COMPLETED("Insertion successful!")
            }
        }
    }

    // Delete Drawings and all markers associated with it
    fun deleteDrawing(drawingID: Int) {
        _uiState.value = UIState.LOADING
        viewModelScope.launch {
            val result = mainRepository.deleteDrawing(drawingID)
            mainRepository.deleteAllMarkersOfDrawingWithID(drawingID)
            mainRepository.deleteAllMarkerImagesWithDrawingID(drawingID)
            if (result == -1) {
                _uiState.value = UIState.ERROR("Deletion failed!")
            } else {
                _uiState.value = UIState.COMPLETED("Deletion successful!")
            }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}