package com.comixa.core.domain.usecase

import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.source.ComicSource

class ScanLibraryUseCase(
    private val source: ComicSource,
    private val repository: ComicRepository,
) {
    suspend operator fun invoke(): List<ComicBook> {
        val books = source.scan()
        books.forEach { repository.upsert(it) }
        return books
    }
}
