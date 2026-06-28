package com.comixa.feature.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.repository.ProgressRepository
import com.comixa.core.domain.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShelfDetailUiState(
    val shelfName: String = "",
    val books: List<BookWithProgress> = emptyList(),
    val availableBooks: List<ComicBook> = emptyList(),
    val showAddSheet: Boolean = false,
)

@HiltViewModel
class ShelfDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val shelfRepository: ShelfRepository,
    comicRepository: ComicRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {

    private val shelfId: Long = checkNotNull(savedStateHandle["shelfId"])
    private val _showAddSheet = MutableStateFlow(false)

    val uiState: StateFlow<ShelfDetailUiState> = combine(
        shelfRepository.getById(shelfId),
        shelfRepository.getBooksInShelf(shelfId),
        comicRepository.getAll(),
        progressRepository.getAll(),
        _showAddSheet,
    ) { shelf, shelfBooks, allBooks, allProgress, showSheet ->
        val progressMap = allProgress.associateBy { it.bookId }
        val shelfBookIds = shelfBooks.map { it.id }.toSet()
        ShelfDetailUiState(
            shelfName = shelf?.name ?: "",
            books = shelfBooks.map { BookWithProgress(it, progressMap[it.id]) },
            availableBooks = allBooks.filter { it.id !in shelfBookIds },
            showAddSheet = showSheet,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShelfDetailUiState(),
    )

    fun showAddSheet() = _showAddSheet.update { true }
    fun dismissAddSheet() = _showAddSheet.update { false }

    fun addBook(bookId: Long) {
        viewModelScope.launch { shelfRepository.addBook(shelfId, bookId) }
    }

    fun removeBook(bookId: Long) {
        viewModelScope.launch { shelfRepository.removeBook(shelfId, bookId) }
    }
}
