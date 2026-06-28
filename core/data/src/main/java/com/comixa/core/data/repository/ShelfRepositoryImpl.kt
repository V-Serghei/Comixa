package com.comixa.core.data.repository

import com.comixa.core.data.db.dao.ShelfDao
import com.comixa.core.data.db.dao.ShelfWithCount
import com.comixa.core.data.db.entity.ShelfEntity
import com.comixa.core.data.db.entity.ShelfEntryEntity
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.Shelf
import com.comixa.core.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShelfRepositoryImpl @Inject constructor(
    private val dao: ShelfDao,
) : ShelfRepository {

    override fun getAll(): Flow<List<Shelf>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getById(shelfId: Long): Flow<Shelf?> =
        dao.getById(shelfId).map { it?.toDomain() }

    override fun getBooksInShelf(shelfId: Long): Flow<List<ComicBook>> =
        dao.getBooksInShelf(shelfId).map { list -> list.map { it.toDomain() } }

    override suspend fun create(name: String): Long =
        dao.insertShelf(ShelfEntity(name = name, createdAt = System.currentTimeMillis()))

    override suspend fun rename(shelfId: Long, name: String) = dao.rename(shelfId, name)

    override suspend fun delete(shelfId: Long) = dao.delete(shelfId)

    override suspend fun addBook(shelfId: Long, bookId: Long) =
        dao.addBook(ShelfEntryEntity(shelfId = shelfId, bookId = bookId))

    override suspend fun removeBook(shelfId: Long, bookId: Long) =
        dao.removeBook(shelfId, bookId)
}

private fun ShelfWithCount.toDomain() = Shelf(
    id = id,
    name = name,
    createdAt = createdAt,
    bookCount = bookCount,
)
