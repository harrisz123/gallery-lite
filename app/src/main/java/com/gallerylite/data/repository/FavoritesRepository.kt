package com.gallerylite.data.repository

import com.gallerylite.data.local.dao.FavoritesDao
import com.gallerylite.data.local.entity.FavoriteEntity
import com.gallerylite.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepository(
    private val favoritesDao: FavoritesDao,
    private val mediaRepository: MediaRepository
) {
    fun getFavoriteMedia(): Flow<List<MediaItem>> {
        return favoritesDao.getAllFavoriteIds().map { ids ->
            val idSet = ids.toSet()
            mediaRepository.queryAllMedia().filter { idSet.contains(it.id) }
                .map { it.copy(isFavorite = true) }
        }
    }

    suspend fun toggleFavorite(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        if (mediaItem.isFavorite) {
            favoritesDao.removeFavorite(mediaItem.id)
        } else {
            favoritesDao.addFavorite(
                FavoriteEntity(
                    mediaId = mediaItem.id,
                    uriString = mediaItem.uri.toString()
                )
            )
        }
    }
}
