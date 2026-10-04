package com.gallerylite.ui.screens.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerylite.data.model.Album
import com.gallerylite.data.repository.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AlbumsUiState {
    object Loading : AlbumsUiState
    data class Success(val albums: List<Album>) : AlbumsUiState
    object Empty : AlbumsUiState
}

class AlbumsViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumsUiState.Loading
            val albums = albumRepository.getAlbums()
            if (albums.isEmpty()) {
                _uiState.value = AlbumsUiState.Empty
            } else {
                _uiState.value = AlbumsUiState.Success(albums)
            }
        }
    }
}
