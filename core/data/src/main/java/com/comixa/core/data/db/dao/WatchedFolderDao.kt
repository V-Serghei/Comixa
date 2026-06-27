package com.comixa.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.comixa.core.data.db.entity.WatchedFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedFolderDao {

    @Query("SELECT * FROM watched_folders ORDER BY addedAt ASC")
    fun getAll(): Flow<List<WatchedFolderEntity>>

    @Query("SELECT * FROM watched_folders ORDER BY addedAt ASC")
    suspend fun getAllSnapshot(): List<WatchedFolderEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: WatchedFolderEntity): Long

    @Query("DELETE FROM watched_folders WHERE path = :path")
    suspend fun deleteByPath(path: String)
}
