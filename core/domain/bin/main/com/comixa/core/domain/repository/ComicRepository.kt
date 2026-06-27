package com.comixa.core.domain.repository

import com.comixa.core.domain.model.ComicBook
import kotlinx.coroutines.flow.Flow

interface ComicRepository {
    fun getAll(): Flow<List<ComicBook>>
    suspend fun getById(id: Long): ComicBook?
    suspend fun insertIfNotExists(book: ComicBook): Long
    suspend fun upsert(book: ComicBook): Long
    suspend fun delete(bookId: Long)
    suspend fun updatePageCount(bookId: Long, pageCount: Int)
}
