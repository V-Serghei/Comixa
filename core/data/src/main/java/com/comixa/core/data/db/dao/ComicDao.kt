package com.comixa.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.comixa.core.data.db.entity.ComicBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {
    @Query("SELECT * FROM comic_books ORDER BY title ASC")
    fun getAll(): Flow<List<ComicBookEntity>>

    @Query("SELECT * FROM comic_books WHERE id = :id")
    suspend fun getById(id: Long): ComicBookEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(entity: ComicBookEntity): Long

    @Upsert
    suspend fun upsert(entity: ComicBookEntity): Long

    @Query("DELETE FROM comic_books WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE comic_books SET pageCount = :count WHERE id = :id")
    suspend fun updatePageCount(id: Long, count: Int)

    @Query("SELECT * FROM comic_books WHERE seriesName = :seriesName ORDER BY issueNumber ASC, title ASC")
    fun getBySeries(seriesName: String): Flow<List<ComicBookEntity>>
}
