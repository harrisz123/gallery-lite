package com.gallerylite.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gallerylite.data.local.dao.FavoritesDao
import com.gallerylite.data.local.dao.TrashDao
import com.gallerylite.data.local.dao.VaultDao
import com.gallerylite.data.local.entity.FavoriteEntity
import com.gallerylite.data.local.entity.TrashEntity
import com.gallerylite.data.local.entity.VaultEntity

@Database(
    entities = [FavoriteEntity::class, TrashEntity::class, VaultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GalleryDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun trashDao(): TrashDao
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: GalleryDatabase? = null

        fun getDatabase(context: Context): GalleryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GalleryDatabase::class.java,
                    "gallery_lite.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
