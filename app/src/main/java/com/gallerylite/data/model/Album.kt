package com.gallerylite.data.model

import android.net.Uri

data class Album(
    val id: String,
    val name: String,
    val coverUri: Uri,
    val mediaCount: Int,
    val lastModifiedMs: Long
)

data class DateGroup(
    val title: String, // e.g. "Today", "Yesterday", "Oct 3, 2026"
    val timestamp: Long,
    val items: List<MediaItem>
)

data class OnThisDayMemory(
    val yearsAgo: Int,
    val year: Int,
    val dateDisplay: String, // e.g. "5 October 2023"
    val items: List<MediaItem>
)
