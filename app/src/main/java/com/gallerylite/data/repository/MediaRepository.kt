package com.gallerylite.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gallerylite.data.local.dao.FavoritesDao
import com.gallerylite.data.model.DateGroup
import com.gallerylite.data.model.MediaItem
import com.gallerylite.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MediaRepository(
    private val context: Context,
    private val favoritesDao: FavoritesDao
) {

    fun getGroupedMediaStream(): Flow<List<DateGroup>> {
        val rawMediaFlow = flow {
            emit(queryAllMediaInternal())
        }

        return combine(rawMediaFlow, favoritesDao.getAllFavoriteIds()) { mediaList, favoriteIds ->
            val favSet = favoriteIds.toSet()
            val enrichedList = mediaList.map { item ->
                if (favSet.contains(item.id)) item.copy(isFavorite = true) else item
            }

            // Group by human-friendly date title (Today, Yesterday, 4 October, etc.)
            enrichedList
                .groupBy { DateUtils.formatToDateGroupTitle(it.dateTakenMs) }
                .map { (title, items) ->
                    DateGroup(
                        title = title,
                        timestamp = items.firstOrNull()?.dateTakenMs ?: 0L,
                        items = items
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun queryAllMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        queryAllMediaInternal()
    }

    private fun queryAllMediaInternal(): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DURATION
        )

        // Select both images and videos
        val selection = "(${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?) AND ${MediaStore.Files.FileColumns.SIZE} > 0"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val dateModifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val durationCol = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val mimeType = cursor.getString(mimeCol) ?: "image/jpeg"
                    val contentUri = if (mimeType.startsWith("video")) {
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    } else {
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    }

                    val dateAddedSec = cursor.getLong(dateAddedCol)
                    val dateModifiedSec = cursor.getLong(dateModifiedCol)
                    val finalDateMs = if (dateModifiedSec > 0) dateModifiedSec * 1000 else dateAddedSec * 1000

                    val duration = if (durationCol != -1 && !cursor.isNull(durationCol)) {
                        cursor.getLong(durationCol)
                    } else 0L

                    items.add(
                        MediaItem(
                            id = id,
                            uri = contentUri,
                            displayName = cursor.getString(nameCol) ?: "Media_$id",
                            path = cursor.getString(dataCol) ?: "",
                            dateTakenMs = finalDateMs,
                            sizeBytes = cursor.getLong(sizeCol),
                            width = cursor.getInt(widthCol),
                            height = cursor.getInt(heightCol),
                            durationMs = duration,
                            mimeType = mimeType,
                            bucketId = cursor.getString(bucketIdCol) ?: "0",
                            bucketName = cursor.getString(bucketNameCol) ?: "Internal"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }
}
