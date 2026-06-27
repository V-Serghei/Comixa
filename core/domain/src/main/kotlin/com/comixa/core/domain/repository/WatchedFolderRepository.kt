package com.comixa.core.domain.repository

import com.comixa.core.domain.model.WatchedFolder
import kotlinx.coroutines.flow.Flow

interface WatchedFolderRepository {
    fun getAll(): Flow<List<WatchedFolder>>
    suspend fun getSnapshot(): List<WatchedFolder>
    suspend fun add(path: String)
    suspend fun remove(path: String)
}
