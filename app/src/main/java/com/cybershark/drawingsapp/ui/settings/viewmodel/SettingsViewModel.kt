package com.cybershark.drawingsapp.ui.settings.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.drawingsapp.data.repository.MainRepository
import kotlinx.coroutines.launch

class SettingsViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun deleteAllData() = viewModelScope.launch { mainRepository.deleteAddData() }

}
