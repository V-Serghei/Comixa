package com.comixa.core.domain.repository

import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    suspend fun get(bookId: Long): ReadingProgress?
    suspend fun save(progress: ReadingProgress)
    suspend fun setStatus(bookId: Long, status: ReadingStatus)
    suspend fun delete(bookId: Long)
    fun getAll(): Flow<List<ReadingProgress>>
    fun getRecentlyRead(limit: Int = 20): Flow<List<ReadingProgress>>
}
