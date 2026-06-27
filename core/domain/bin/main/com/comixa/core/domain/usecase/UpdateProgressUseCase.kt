package com.comixa.core.domain.usecase

import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.repository.ProgressRepository

class UpdateProgressUseCase(
    private val repository: ProgressRepository,
) {
    suspend operator fun invoke(bookId: Long, currentPage: Int, totalPages: Int) {
        repository.save(
            ReadingProgress(
                bookId = bookId,
                currentPage = currentPage,
                totalPages = totalPages,
            )
        )
    }
}
