package com.comixa.core.domain.repository

import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.Shelf
import kotlinx.coroutines.flow.Flow

interface ShelfRepository {
    fun getAll(): Flow<List<Shelf>>
    fun getById(shelfId: Long): Flow<Shelf?>
    fun getBooksInShelf(shelfId: Long): Flow<List<ComicBook>>
    suspend fun create(name: String): Long
    suspend fun rename(shelfId: Long, name: String)
    suspend fun delete(shelfId: Long)
    suspend fun addBook(shelfId: Long, bookId: Long)
    suspend fun removeBook(shelfId: Long, bookId: Long)
}
