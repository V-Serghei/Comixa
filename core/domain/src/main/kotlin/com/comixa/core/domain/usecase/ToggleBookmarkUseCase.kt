package com.comixa.core.domain.usecase

import com.comixa.core.domain.model.Bookmark
import com.comixa.core.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.first

class ToggleBookmarkUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(bookId: Long, pageIndex: Int) {
        val existing = repository.getForBook(bookId).first()
            .find { it.pageIndex == pageIndex }

        if (existing != null) {
            repository.remove(existing.id)
        } else {
            repository.add(Bookmark(bookId = bookId, pageIndex = pageIndex))
        }
    }
}
