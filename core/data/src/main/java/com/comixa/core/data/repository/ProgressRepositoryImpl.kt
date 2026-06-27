package com.comixa.core.data.repository

import com.comixa.core.data.db.dao.ProgressDao
import com.comixa.core.data.db.entity.ReadingProgressEntity
import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val dao: ProgressDao,
) : ProgressRepository {

    override suspend fun get(bookId: Long): ReadingProgress? =
        dao.get(bookId)?.toDomain()

    override suspend fun save(progress: ReadingProgress) =
        dao.upsert(ReadingProgressEntity.fromDomain(progress))

    override fun getAll(): Flow<List<ReadingProgress>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getRecentlyRead(limit: Int): Flow<List<ReadingProgress>> =
        dao.getRecentlyRead(limit).map { list -> list.map { it.toDomain() } }
}
