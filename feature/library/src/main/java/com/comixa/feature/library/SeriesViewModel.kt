package com.comixa.feature.library

import androidx.lifecycle.SavedStateHandle
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

data class SeriesUiState(
    val seriesName: String = "",
    val books: List<BookWithProgress> = emptyList(),
)

@HiltViewModel
class SeriesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    comicRepository: ComicRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    private val seriesName: String = checkNotNull(savedStateHandle["seriesName"])

    val uiState: StateFlow<SeriesUiState> = combine(
        comicRepository.getBySeries(seriesName),
        progressRepository.getAll(),
    ) { books, allProgress ->
        val progressMap = allProgress.associateBy { it.bookId }
        SeriesUiState(
            seriesName = seriesName,
            books = books.map { BookWithProgress(it, progressMap[it.id]) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SeriesUiState(seriesName = seriesName),
    )
}
