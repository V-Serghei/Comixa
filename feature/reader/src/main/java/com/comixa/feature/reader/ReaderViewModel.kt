package com.comixa.feature.reader

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.model.Bookmark
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat
import com.comixa.core.domain.model.ReadingDirection
import com.comixa.core.domain.model.ReadingProgress
import com.comixa.core.domain.model.ReadingStatus
import com.comixa.core.domain.repository.BookmarkRepository
import com.comixa.core.domain.repository.ComicRepository
import com.comixa.core.domain.repository.ProgressRepository
import com.comixa.core.domain.repository.UserPreferencesRepository
import com.github.junrar.Archive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.inject.Inject

data class ReaderUiState(
    val book: ComicBook? = null,
    val pageCount: Int = 0,
    val currentPage: Int = 0,
    val readingStatus: ReadingStatus = ReadingStatus.UNREAD,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val comicRepository: ComicRepository,
    private val progressRepository: ProgressRepository,
    private val bookmarkRepository: BookmarkRepository,
    prefsRepository: UserPreferencesRepository,
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"]) { "bookId arg is required" }

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    val readingDirection: StateFlow<ReadingDirection> = prefsRepository.get()
        .map { it.readingDirection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReadingDirection.LEFT_TO_RIGHT)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val book = comicRepository.getById(bookId)
            if (book == null) {
                _uiState.update { it.copy(isLoading = false, error = "Comic not found") }
                return@launch
            }
            val progress = progressRepository.get(bookId)
            val pageCount = countPages(book)
            if (book.pageCount == 0 && pageCount > 0) {
                comicRepository.updatePageCount(bookId, pageCount)
            }
            _uiState.update {
                it.copy(
                    book = book,
                    pageCount = pageCount,
                    currentPage = progress?.currentPage ?: 0,
                    readingStatus = progress?.status ?: ReadingStatus.UNREAD,
                    isLoading = false,
                )
            }
        }
    }

    fun onPageSettled(page: Int) {
        val pageCount = _uiState.value.pageCount
        if (pageCount == 0) return
        // Don't record progress for page 0 — the book stays UNREAD until the user moves past the first page
        if (page == 0 && _uiState.value.readingStatus == ReadingStatus.UNREAD) return
        val newStatus = when {
            _uiState.value.readingStatus == ReadingStatus.COMPLETED -> ReadingStatus.COMPLETED
            page >= pageCount - 1 -> ReadingStatus.COMPLETED
            else -> ReadingStatus.IN_PROGRESS
        }
        _uiState.update { it.copy(currentPage = page, readingStatus = newStatus) }
        viewModelScope.launch {
            progressRepository.save(
                ReadingProgress(
                    bookId = bookId,
                    currentPage = page,
                    totalPages = pageCount,
                    lastReadAt = System.currentTimeMillis(),
                    status = newStatus,
                )
            )
        }
    }

    fun markAsCompleted() {
        val pageCount = _uiState.value.pageCount
        if (pageCount == 0) return
        _uiState.update { it.copy(readingStatus = ReadingStatus.COMPLETED) }
        viewModelScope.launch {
            progressRepository.save(
                ReadingProgress(
                    bookId = bookId,
                    currentPage = pageCount - 1,
                    totalPages = pageCount,
                    lastReadAt = System.currentTimeMillis(),
                    status = ReadingStatus.COMPLETED,
                )
            )
        }
    }

    fun markAsUnread() {
        _uiState.update { it.copy(readingStatus = ReadingStatus.UNREAD, currentPage = 0) }
        viewModelScope.launch { progressRepository.delete(bookId) }
    }

    fun addBookmarkAtPage(page: Int) {
        viewModelScope.launch {
            bookmarkRepository.add(
                Bookmark(
                    bookId = bookId,
                    pageIndex = page,
                    createdAt = System.currentTimeMillis(),
                )
            )
        }
    }

    private fun countPages(book: ComicBook): Int = try {
        when (book.format) {
            ComicFormat.PDF -> countPdfPages(book.filePath)
            ComicFormat.CBZ, ComicFormat.ZIP -> countZipPages(book.filePath)
            ComicFormat.CBR -> countRarPages(book.filePath)
        }
    } catch (e: Exception) {
        0
    }

    private fun countPdfPages(filePath: String): Int {
        val uri = Uri.parse(filePath)
        val pfd: ParcelFileDescriptor = when (uri.scheme) {
            "file" -> ParcelFileDescriptor.open(File(uri.path!!), ParcelFileDescriptor.MODE_READ_ONLY)
            "content" -> context.contentResolver.openFileDescriptor(uri, "r") ?: return 0
            else -> return 0
        }
        return pfd.use { PdfRenderer(it).use { r -> r.pageCount } }
    }

    private fun countZipPages(filePath: String): Int {
        val uri = Uri.parse(filePath)
        val file = when (uri.scheme) {
            "file" -> File(uri.path!!)
            else -> return 0
        }
        return ZipFile(file).use { zip ->
            zip.fileHeaders.count { !it.isDirectory && isImageFile(it.fileName) }
        }
    }

    private fun countRarPages(filePath: String): Int {
        val uri = Uri.parse(filePath)
        val file = when (uri.scheme) {
            "file" -> File(uri.path!!)
            else -> return 0
        }
        return Archive(file).use { archive ->
            archive.fileHeaders.count { !it.isDirectory && isImageFile(it.fileName) }
        }
    }

    private fun isImageFile(name: String): Boolean {
        val ext = name.substringAfterLast('.', "").lowercase()
        return ext in setOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "avif")
    }
}
