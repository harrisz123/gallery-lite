package com.gallerylite.ui.screens.onthisday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerylite.data.model.OnThisDayMemory
import com.gallerylite.data.repository.OnThisDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OnThisDayUiState {
    object Loading : OnThisDayUiState
    data class Success(val memories: List<OnThisDayMemory>) : OnThisDayUiState
    object Empty : OnThisDayUiState
}

class OnThisDayViewModel(
    private val onThisDayRepository: OnThisDayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnThisDayUiState>(OnThisDayUiState.Loading)
    val uiState: StateFlow<OnThisDayUiState> = _uiState.asStateFlow()

    init {
        loadMemories()
    }

    fun loadMemories() {
        viewModelScope.launch {
            _uiState.value = OnThisDayUiState.Loading
            val memories = onThisDayRepository.getMemoriesForToday()
            if (memories.isEmpty()) {
                _uiState.value = OnThisDayUiState.Empty
            } else {
                _uiState.value = OnThisDayUiState.Success(memories)
            }
        }
    }
}
