package com.comixa.core.data.repository

import com.comixa.core.data.db.dao.ComicDao
import com.comixa.core.data.db.entity.ComicBookEntity
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.repository.ComicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ComicRepositoryImpl @Inject constructor(
    private val dao: ComicDao,
) : ComicRepository {

    override fun getAll(): Flow<List<ComicBook>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): ComicBook? =
        dao.getById(id)?.toDomain()

    override suspend fun insertIfNotExists(book: ComicBook): Long =
        dao.insertIfNotExists(ComicBookEntity.fromDomain(book))

    override suspend fun upsert(book: ComicBook): Long =
        dao.upsert(ComicBookEntity.fromDomain(book))

    override suspend fun delete(bookId: Long) =
        dao.delete(bookId)

    override suspend fun updatePageCount(bookId: Long, pageCount: Int) =
        dao.updatePageCount(bookId, pageCount)
}
