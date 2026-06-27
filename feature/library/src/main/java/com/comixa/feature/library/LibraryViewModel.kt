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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookWithProgress(
    val book: ComicBook,
    val progress: ReadingProgress?,
)

data class LibraryUiState(
    val books: List<BookWithProgress> = emptyList(),
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
        LibraryUiState(
            books = books.map { BookWithProgress(it, progressMap[it.id]) },
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
}
