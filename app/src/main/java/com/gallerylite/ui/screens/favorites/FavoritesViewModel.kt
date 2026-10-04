package com.gallerylite.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerylite.data.model.MediaItem
import com.gallerylite.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    data class Success(val items: List<MediaItem>) : FavoritesUiState
    object Empty : FavoritesUiState
}

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            favoritesRepository.getFavoriteMedia().collect { favorites ->
                if (favorites.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    _uiState.value = FavoritesUiState.Success(favorites)
                }
            }
        }
    }
}
