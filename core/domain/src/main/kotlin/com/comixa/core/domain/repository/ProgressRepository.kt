package com.comixa.core.domain.repository

import com.comixa.core.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    suspend fun get(bookId: Long): ReadingProgress?
    suspend fun save(progress: ReadingProgress)
    fun getAll(): Flow<List<ReadingProgress>>
    fun getRecentlyRead(limit: Int = 20): Flow<List<ReadingProgress>>
}
