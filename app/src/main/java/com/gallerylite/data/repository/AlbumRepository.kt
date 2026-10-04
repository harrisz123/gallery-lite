package com.gallerylite.data.repository

import com.gallerylite.data.model.Album
import com.gallerylite.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val mediaRepository: MediaRepository
) {
    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val allMedia = mediaRepository.queryAllMedia()

        allMedia.groupBy { it.bucketId }
            .mapNotNull { (bucketId, items) ->
                if (items.isEmpty()) return@mapNotNull null
                val firstItem = items.first()
                Album(
                    id = bucketId,
                    name = firstItem.bucketName.ifEmpty { "Pictures" },
                    coverUri = firstItem.uri,
                    mediaCount = items.size,
                    lastModifiedMs = firstItem.dateTakenMs
                )
            }
            .sortedByDescending { it.mediaCount }
    }

    suspend fun getMediaForAlbum(bucketId: String): List<MediaItem> = withContext(Dispatchers.IO) {
        mediaRepository.queryAllMedia().filter { it.bucketId == bucketId }
    }
}
