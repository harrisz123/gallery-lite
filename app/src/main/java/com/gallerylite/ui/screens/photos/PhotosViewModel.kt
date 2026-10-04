package com.gallerylite.ui.screens.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerylite.data.model.DateGroup
import com.gallerylite.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface PhotosUiState {
    object Loading : PhotosUiState
    data class Success(val dateGroups: List<DateGroup>) : PhotosUiState
    data class Empty(val message: String = "No photos or videos found") : PhotosUiState
    data class Error(val errorMsg: String) : PhotosUiState
}

class PhotosViewModel(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotosUiState>(PhotosUiState.Loading)
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    fun loadMedia() {
        viewModelScope.launch {
            _uiState.value = PhotosUiState.Loading
            mediaRepository.getGroupedMediaStream()
                .catch { e ->
                    _uiState.value = PhotosUiState.Error(e.message ?: "Failed to load media")
                }
                .collect { groups ->
                    if (groups.isEmpty() || groups.all { it.items.isEmpty() }) {
                        _uiState.value = PhotosUiState.Empty()
                    } else {
                        _uiState.value = PhotosUiState.Success(groups)
                    }
                }
        }
    }
}
