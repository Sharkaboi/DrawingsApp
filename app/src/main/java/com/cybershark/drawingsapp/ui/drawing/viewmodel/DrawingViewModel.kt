package com.cybershark.drawingsapp.ui.drawing.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.data.repository.MainRepository
import com.cybershark.drawingsapp.util.UIState

class DrawingViewModel
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

}