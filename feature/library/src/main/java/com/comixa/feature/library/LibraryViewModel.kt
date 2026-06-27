package com.comixa.feature.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.data.source.LocalFolderSource
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.repository.ProgressRepository
import com.comixa.core.domain.repository.WatchedFolderRepository
import com.comixa.core.domain.usecase.ScanLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookWithProgress(
    val book: ComicBook,
    val progress: ReadingProgress?,
)

sealed interface LibraryItem {
    data class Single(val item: BookWithProgress) : LibraryItem

    data class Series(
        val name: String,
        val books: List<BookWithProgress>,
    ) : LibraryItem {
        val cover: BookWithProgress get() = books.minByOrNull { it.book.issueNumber ?: Int.MAX_VALUE }!!
        val readCount: Int get() = books.count { it.progress != null }
    }
}

data class LibraryUiState(
    val items: List<LibraryItem> = emptyList(),
    val isScanning: Boolean = false,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: ComicRepository,
    private val scanUseCase: ScanLibraryUseCase,
    private val localSource: LocalFolderSource,
    watchedFolderRepository: WatchedFolderRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    private var scanJob: Job? = null

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.getAll(),
        progressRepository.getAll(),
        _isScanning,
    ) { books, allProgress, scanning ->
        val progressMap = allProgress.associateBy { it.bookId }
        val withProgress = books.map { BookWithProgress(it, progressMap[it.id]) }
        LibraryUiState(
            items = groupIntoLibraryItems(withProgress),
            isScanning = scanning,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState(),
    )

    init {
        viewModelScope.launch {
            watchedFolderRepository.getAll()
                .distinctUntilChanged()
                .collect { startScan() }
        }
    }

    fun onFolderSelected(uri: Uri) {
        localSource.setRootUri(uri)
        startScan()
    }

    fun startScan() {
        if (scanJob?.isActive == true) return
        scanJob = viewModelScope.launch {
            _isScanning.update { true }
            try {
                scanUseCase().collect { }
            } finally {
                _isScanning.update { false }
            }
        }
    }

    private fun groupIntoLibraryItems(books: List<BookWithProgress>): List<LibraryItem> {
        val grouped = books.groupBy { it.book.seriesName }

        val singles = grouped[null]?.map { LibraryItem.Single(it) } ?: emptyList()

        val seriesAndSingles = grouped
            .filterKeys { it != null }
            .flatMap { (name, seriesBooks) ->
                val sorted = seriesBooks.sortedBy { it.book.issueNumber ?: Int.MAX_VALUE }
                if (sorted.size == 1) {
                    listOf(LibraryItem.Single(sorted.first()))
                } else {
                    listOf(LibraryItem.Series(name!!, sorted))
                }
            }

        return (singles + seriesAndSingles).sortedBy { item ->
            when (item) {
                is LibraryItem.Single -> item.item.book.seriesName?.lowercase()
                    ?: item.item.book.title.lowercase()
                is LibraryItem.Series -> item.name.lowercase()
            }
        }
    }
}
