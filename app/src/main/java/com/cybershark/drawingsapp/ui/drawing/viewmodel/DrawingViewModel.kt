package com.cybershark.drawingsapp.ui.drawing.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cybershark.drawingsapp.data.models.DrawingEntity
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

    fun insertMarker() {}

    fun updateMarker() {}

    companion object{
        const val TAG ="DrawingViewModel"
        const val INTENT_ID_KEY = "drawingID"
    }
}