package com.comixa.feature.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.data.source.LocalFolderSource
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat
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

enum class SortOrder {
    TITLE_ASC, TITLE_DESC, RECENTLY_ADDED, RECENTLY_READ
}

data class LibraryFilterState(
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE_ASC,
    val showUnreadOnly: Boolean = false,
    val formatFilter: ComicFormat? = null,
)

data class LibraryUiState(
    val items: List<LibraryItem> = emptyList(),
    val isScanning: Boolean = false,
    val filter: LibraryFilterState = LibraryFilterState(),
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
    private val _filter = MutableStateFlow(LibraryFilterState())
    private var scanJob: Job? = null

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.getAll(),
        progressRepository.getAll(),
        _isScanning,
        _filter,
    ) { books, allProgress, scanning, filter ->
        val progressMap = allProgress.associateBy { it.bookId }
        val withProgress = books.map { BookWithProgress(it, progressMap[it.id]) }
        val grouped = groupIntoLibraryItems(withProgress)
        val displayed = applyFilter(grouped, filter)
        LibraryUiState(items = displayed, isScanning = scanning, filter = filter)
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

    fun setSearchQuery(query: String) = _filter.update { it.copy(searchQuery = query) }
    fun setSortOrder(order: SortOrder) = _filter.update { it.copy(sortOrder = order) }
    fun toggleUnreadOnly() = _filter.update { it.copy(showUnreadOnly = !it.showUnreadOnly) }
    fun setFormatFilter(format: ComicFormat?) = _filter.update { it.copy(formatFilter = format) }

    private fun groupIntoLibraryItems(books: List<BookWithProgress>): List<LibraryItem> {
        val grouped = books.groupBy { it.book.seriesName }
        val singles = grouped[null]?.map { LibraryItem.Single(it) } ?: emptyList()
        val rest = grouped.filterKeys { it != null }.flatMap { (name, seriesBooks) ->
            val sorted = seriesBooks.sortedBy { it.book.issueNumber ?: Int.MAX_VALUE }
            if (sorted.size == 1) listOf(LibraryItem.Single(sorted.first()))
            else listOf(LibraryItem.Series(name!!, sorted))
        }
        return singles + rest
    }

    private fun applyFilter(items: List<LibraryItem>, filter: LibraryFilterState): List<LibraryItem> {
        var result = items

        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            result = result.filter { item ->
                when (item) {
                    is LibraryItem.Single ->
                        item.item.book.title.lowercase().contains(q) ||
                        item.item.book.seriesName?.lowercase()?.contains(q) == true
                    is LibraryItem.Series ->
                        item.name.lowercase().contains(q) ||
                        item.books.any { it.book.title.lowercase().contains(q) }
                }
            }
        }

        if (filter.showUnreadOnly) {
            result = result.filter { item ->
                when (item) {
                    is LibraryItem.Single -> item.item.progress == null
                    is LibraryItem.Series -> item.books.any { it.progress == null }
                }
            }
        }

        filter.formatFilter?.let { fmt ->
            result = result.filter { item ->
                when (item) {
                    is LibraryItem.Single -> item.item.book.format == fmt
                    is LibraryItem.Series -> item.books.any { it.book.format == fmt }
                }
            }
        }

        return when (filter.sortOrder) {
            SortOrder.TITLE_ASC -> result.sortedBy { it.sortKey.lowercase() }
            SortOrder.TITLE_DESC -> result.sortedByDescending { it.sortKey.lowercase() }
            SortOrder.RECENTLY_ADDED -> result.sortedByDescending { it.addedAt }
            SortOrder.RECENTLY_READ -> result.sortedByDescending { it.lastReadAt }
        }
    }

    private val LibraryItem.sortKey: String
        get() = when (this) {
            is LibraryItem.Single -> item.book.seriesName ?: item.book.title
            is LibraryItem.Series -> name
        }

    private val LibraryItem.addedAt: Long
        get() = when (this) {
            is LibraryItem.Single -> item.book.addedAt
            is LibraryItem.Series -> books.maxOf { it.book.addedAt }
        }

    private val LibraryItem.lastReadAt: Long
        get() = when (this) {
            is LibraryItem.Single -> item.progress?.lastReadAt ?: 0L
            is LibraryItem.Series -> books.maxOfOrNull { it.progress?.lastReadAt ?: 0L } ?: 0L
        }
}
