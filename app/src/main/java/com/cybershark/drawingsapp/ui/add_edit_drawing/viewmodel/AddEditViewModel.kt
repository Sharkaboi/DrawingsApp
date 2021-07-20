package com.cybershark.drawingsapp.ui.add_edit_drawing.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.drawingsapp.data.room.entities.DrawingEntity
import com.cybershark.drawingsapp.ui.add_edit_drawing.repository.IAddEditRepository
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel
@Inject
constructor(
    private val addEditRepository: IAddEditRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState
    private val _currentDrawing = MutableLiveData(DrawingEntity(title = "", imageURI = Uri.EMPTY, timeAdded = Date(0)))
    val currentDrawing: LiveData<DrawingEntity> = _currentDrawing

    fun initDrawing(drawingId: Int) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = addEditRepository.getDrawingById(drawingId)
            if (result.isFailure) {
                _uiState.setError(result.exceptionOrNull())
            } else {
                _currentDrawing.value = result.getOrNull()
                _uiState.getDefault()
            }
        }
    }

    fun confirmUpdateDrawing(title: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = addEditRepository.updateDrawing(
                _currentDrawing.value?.copy(
                    title = title,
                    timeAdded = Date()
                )
            )
            if (result.isFailure) {
                _uiState.setError(result.exceptionOrNull())
            } else {
                _uiState.setSuccess("")
            }
        }
    }

    fun setCurrentImageUri(uri: Uri) {
        _currentDrawing.value = _currentDrawing.value?.copy(
            imageURI = uri
        )
    }

    fun confirmAddDrawing(title: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = addEditRepository.addDrawing(
                _currentDrawing.value?.copy(
                    title = title,
                    timeAdded = Date()
                )
            )
            if (result.isFailure) {
                _uiState.setError(result.exceptionOrNull())
            } else {
                _uiState.setSuccess("")
            }
        }
    }
}