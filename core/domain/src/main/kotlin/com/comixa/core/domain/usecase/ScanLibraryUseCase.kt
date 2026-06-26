package com.comixa.core.domain.usecase

import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.source.ComicSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class ScanLibraryUseCase(
    private val source: ComicSource,
    private val repository: ComicRepository,
) {
    operator fun invoke(): Flow<ComicBook> = source.scan()
        .onEach { repository.insertIfNotExists(it) }
}
