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

@HiltViewModel
class SeriesListViewModel @Inject constructor(
    repository: ComicRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    val series: StateFlow<List<LibraryItem.Series>> = combine(
        repository.getAll(),
        progressRepository.getAll(),
    ) { books, allProgress ->
        val progressMap = allProgress.associateBy { it.bookId }
        books
            .filter { it.seriesName != null }
            .groupBy { it.seriesName!! }
            .filter { (_, grouped) -> grouped.size >= 2 }
            .map { (name, grouped) ->
                LibraryItem.Series(
                    name = name,
                    books = grouped
                        .sortedBy { it.issueNumber ?: Int.MAX_VALUE }
                        .map { BookWithProgress(it, progressMap[it.id]) },
                )
            }
            .sortedBy { it.name.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
}
