package com.comixa.core.data.repository

import com.comixa.core.data.db.dao.WatchedFolderDao
import com.comixa.core.data.db.entity.WatchedFolderEntity
import com.comixa.core.domain.model.WatchedFolder
import com.comixa.core.domain.repository.WatchedFolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchedFolderRepositoryImpl @Inject constructor(
    private val dao: WatchedFolderDao,
) : WatchedFolderRepository {

    override fun getAll(): Flow<List<WatchedFolder>> =
        dao.getAll().map { list -> list.map { it.toModel() } }

    override suspend fun getSnapshot(): List<WatchedFolder> =
        dao.getAllSnapshot().map { it.toModel() }

    override suspend fun add(path: String) {
        dao.insert(WatchedFolderEntity(path = path, addedAt = System.currentTimeMillis()))
    }

    override suspend fun remove(path: String) {
        dao.deleteByPath(path)
    }

    private fun WatchedFolderEntity.toModel() = WatchedFolder(id = id, path = path, addedAt = addedAt)
}
