package com.cybershark.drawingsapp.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.drawingsapp.ui.settings.repository.ISettingsRepository
import com.cybershark.drawingsapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState

    // Clears data from all tables.
    fun deleteAllData() {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = settingsRepository.deleteAddData()
            if (result.isFailure) {
                _uiState.setError(result.exceptionOrNull())
            } else {
                _uiState.setSuccess("")
            }
        }
    }

}
