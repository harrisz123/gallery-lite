package com.gallerylite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gallerylite.data.local.entity.FavoriteEntity
import com.gallerylite.data.local.entity.TrashEntity
import com.gallerylite.data.local.entity.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites ORDER BY addedAtMs DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
    fun isFavorite(mediaId: Long): Flow<Boolean>

    @Query("SELECT mediaId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
    suspend fun removeFavorite(mediaId: Long)
}

@Dao
interface TrashDao {
    @Query("SELECT * FROM trash ORDER BY deletedAtMs DESC")
    fun getAllTrash(): Flow<List<TrashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun moveToTrash(item: TrashEntity)

    @Query("DELETE FROM trash WHERE mediaId = :mediaId")
    suspend fun restoreOrDelete(mediaId: Long)

    @Query("DELETE FROM trash WHERE deletedAtMs < :cutoffMs")
    suspend fun purgeExpired(cutoffMs: Long)
}

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault ORDER BY hiddenAtMs DESC")
    fun getAllHidden(): Flow<List<VaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun hideItem(vaultItem: VaultEntity)

    @Query("DELETE FROM vault WHERE id = :id")
    suspend fun unhideItem(id: Long)
}
