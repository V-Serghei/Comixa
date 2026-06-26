package com.comixa.core.domain.repository

import com.comixa.core.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getForBook(bookId: Long): Flow<List<Bookmark>>
    suspend fun add(bookmark: Bookmark): Long
    suspend fun remove(bookmarkId: Long)
}
