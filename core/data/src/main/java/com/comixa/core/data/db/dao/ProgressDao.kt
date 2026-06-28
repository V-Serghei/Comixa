package com.comixa.core.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.comixa.core.data.db.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun get(bookId: Long): ReadingProgressEntity?

    @Upsert
    suspend fun upsert(entity: ReadingProgressEntity)

    @Query("UPDATE reading_progress SET status = :status WHERE bookId = :bookId")
    suspend fun updateStatus(bookId: Long, status: String)

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("SELECT * FROM reading_progress")
    fun getAll(): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM reading_progress ORDER BY lastReadAt DESC LIMIT :limit")
    fun getRecentlyRead(limit: Int): Flow<List<ReadingProgressEntity>>
}
