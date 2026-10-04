package com.gallerylite.data.model

import android.net.Uri

enum class MediaType {
    IMAGE,
    VIDEO
}

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val path: String,
    val dateTakenMs: Long,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val durationMs: Long = 0L,
    val mimeType: String,
    val bucketId: String,
    val bucketName: String,
    val isFavorite: Boolean = false
) {
    val isVideo: Boolean
        get() = mimeType.startsWith("video/")

    val aspectRatio: Float
        get() = if (height > 0 && width > 0) width.toFloat() / height.toFloat() else 1.0f

    val formattedDuration: String
        get() {
            if (durationMs <= 0) return ""
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
