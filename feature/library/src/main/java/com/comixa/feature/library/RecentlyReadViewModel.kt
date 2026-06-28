package com.comixa.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecentlyReadUiState(
    val items: List<BookWithProgress> = emptyList(),
)

@HiltViewModel
class RecentlyReadViewModel @Inject constructor(
    comicRepository: ComicRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<RecentlyReadUiState> = combine(
        comicRepository.getAll(),
        progressRepository.getRecentlyRead(limit = 50),
    ) { books, recentProgress ->
        val bookMap = books.associateBy { it.id }
        val items = recentProgress.mapNotNull { progress ->
            bookMap[progress.bookId]?.let { book ->
                BookWithProgress(book = book, progress = progress)
            }
        }
        RecentlyReadUiState(items = items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecentlyReadUiState(),
    )
}
