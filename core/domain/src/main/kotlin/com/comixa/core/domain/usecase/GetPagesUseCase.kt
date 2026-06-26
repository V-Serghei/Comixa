package com.comixa.core.domain.usecase

import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.Page
import com.comixa.core.domain.source.ComicSource

class GetPagesUseCase(
    private val source: ComicSource,
) {
    suspend operator fun invoke(book: ComicBook): List<Page> {
        val count = source.getPageCount(book)
        return List(count) { index -> Page(index = index, bookId = book.id) }
    }
}
