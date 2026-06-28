package com.comixa.core.data.db.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.comixa.core.data.db.entity.ComicBookEntity
import com.comixa.core.data.db.entity.ShelfEntryEntity
import com.comixa.core.data.db.entity.ShelfEntity
import kotlinx.coroutines.flow.Flow

data class ShelfWithCount(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "bookCount") val bookCount: Int,
)

@Dao
interface ShelfDao {

    @Query("""
        SELECT s.id, s.name, s.createdAt, COUNT(e.bookId) AS bookCount
        FROM shelves s
        LEFT JOIN shelf_entries e ON e.shelfId = s.id
        GROUP BY s.id
        ORDER BY s.createdAt DESC
    """)
    fun getAll(): Flow<List<ShelfWithCount>>

    @Query("""
        SELECT s.id, s.name, s.createdAt, COUNT(e.bookId) AS bookCount
        FROM shelves s
        LEFT JOIN shelf_entries e ON e.shelfId = s.id
        WHERE s.id = :shelfId
        GROUP BY s.id
    """)
    fun getById(shelfId: Long): Flow<ShelfWithCount?>

    @Query("""
        SELECT cb.* FROM comic_books cb
        INNER JOIN shelf_entries e ON cb.id = e.bookId
        WHERE e.shelfId = :shelfId
        ORDER BY cb.title ASC
    """)
    fun getBooksInShelf(shelfId: Long): Flow<List<ComicBookEntity>>

    @Insert
    suspend fun insertShelf(shelf: ShelfEntity): Long

    @Query("UPDATE shelves SET name = :name WHERE id = :shelfId")
    suspend fun rename(shelfId: Long, name: String)

    @Query("DELETE FROM shelves WHERE id = :shelfId")
    suspend fun delete(shelfId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(entry: ShelfEntryEntity)

    @Query("DELETE FROM shelf_entries WHERE shelfId = :shelfId AND bookId = :bookId")
    suspend fun removeBook(shelfId: Long, bookId: Long)
}
