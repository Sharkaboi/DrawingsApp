package com.cybershark.drawingsapp.ui.main.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState
    private val _drawingsList = mainRepository.drawingsList
    val drawingsList: LiveData<List<DrawingEntity>> = _drawingsList

    fun getDrawingByID(drawingID: Int): DrawingEntity {
        return _drawingsList.value!!.first { it.id == drawingID }
    }

    fun updateDrawing(drawingEntity: DrawingEntity) {
        _uiState.setLoading()
        viewModelScope.launch {
            val previousDrawingEntity = _drawingsList.value?.first { it.id == drawingEntity.id }
            // Deleting all markers and marker images of drawing if image is updated.
            if (previousDrawingEntity?.imageURI != drawingEntity.imageURI) {
                mainRepository.deleteAllMarkerImagesWithDrawingID(drawingEntity.id)
                mainRepository.deleteAllMarkersOfDrawingWithID(drawingEntity.id)
                val newEntityWithZeroMarkerCount: DrawingEntity = drawingEntity.copy(markerCount = 0)
                val result = mainRepository.updateDrawing(newEntityWithZeroMarkerCount)
                if (result == -1) {
                    _uiState.setError("Updation failed!")
                } else {
                    _uiState.setSuccess("Updation successful!")
                }
            } else {
                val result = mainRepository.updateDrawing(drawingEntity)
                if (result == -1) {
                    _uiState.setError("Updation failed!")
                } else {
                    _uiState.setSuccess("Updation successful!")
                }
            }
        }
    }

    fun insertDrawing(title: String, uri: Uri, date: Date) {
        _uiState.setLoading()
        val drawingItem = DrawingEntity(
            title = title,
            imageURI = uri,
            timeAdded = date,
            markerCount = 0
        )
        viewModelScope.launch {
            val result = mainRepository.insertDrawing(drawingItem)
            if (result == -1L) {
                _uiState.setError("Insertion failed!")
            } else {
                _uiState.setSuccess("Insertion successful!")
            }
        }
    }

    // Delete Drawings and all markers associated with it
    fun deleteDrawing(drawingID: Int) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = mainRepository.deleteDrawing(drawingID)
            mainRepository.deleteAllMarkersOfDrawingWithID(drawingID)
            mainRepository.deleteAllMarkerImagesWithDrawingID(drawingID)
            if (result == -1) {
                _uiState.setError("Deletion failed!")
            } else {
                _uiState.setSuccess("Deletion successful!")
            }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}