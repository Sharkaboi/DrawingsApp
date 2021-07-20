package com.cybershark.drawingsapp.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.ui.main.repository.IMainRepository
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val mainRepository: IMainRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState
    private val _drawingsList = mainRepository.drawingsList
    val drawingsList: LiveData<List<DrawingsWithMarkersAndMarkerImages>> = _drawingsList

    // Delete Drawings and all markers associated with it
    fun deleteDrawing(drawing: DrawingsWithMarkersAndMarkerImages) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = mainRepository.deleteDrawingAndAllMarkers(drawing)
            if (result.isFailure) {
                _uiState.setError("Deletion failed!")
            } else {
                _uiState.setSuccess("Deletion successful!")
            }
        }
    }
}