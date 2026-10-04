package com.gallerylite.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val mediaId: Long,
    val uriString: String,
    val addedAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "trash")
data class TrashEntity(
    @PrimaryKey val mediaId: Long,
    val originalPath: String,
    val uriString: String,
    val deletedAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "vault")
data class VaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalMediaId: Long,
    val internalEncryptedPath: String,
    val originalDisplayName: String,
    val mimeType: String,
    val hiddenAtMs: Long = System.currentTimeMillis()
)
