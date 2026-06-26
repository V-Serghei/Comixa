package com.comixa.core.data.repository

import com.comixa.core.data.db.dao.BookmarkDao
import com.comixa.core.data.db.entity.BookmarkEntity
import com.comixa.core.domain.model.Bookmark
import com.comixa.core.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao,
) : BookmarkRepository {

    override fun getForBook(bookId: Long): Flow<List<Bookmark>> =
        dao.getForBook(bookId).map { list -> list.map { it.toDomain() } }

    override suspend fun add(bookmark: Bookmark): Long =
        dao.insert(BookmarkEntity.fromDomain(bookmark))

    override suspend fun remove(bookmarkId: Long) =
        dao.delete(bookmarkId)
}
