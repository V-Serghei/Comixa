package com.comixa.core.data.source

import android.content.Context
import android.net.Uri
import com.comixa.core.data.scanner.FileScanner
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.source.ComicSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

/**
 * Реализация ComicSource для локальных файлов.
 * Пользователь выбирает папку через SAF (ACTION_OPEN_DOCUMENT_TREE),
 * мы сохраняем URI и сканируем через FileScanner.
 */
class LocalFolderSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanner: FileScanner,
) : ComicSource {

    override val sourceId: String = "local"
    override val displayName: String = "Local Library"

    private var rootUri: Uri? = null

    fun setRootUri(uri: Uri) {
        // Сохраняем постоянное разрешение на доступ к папке
        context.contentResolver.takePersistableUriPermission(
            uri,
            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
        rootUri = uri
    }

    override suspend fun scan(): List<ComicBook> {
        val uri = rootUri ?: return emptyList()
        val now = System.currentTimeMillis()
        return scanner.scan(uri)
            .toList()
            .map { it.toComicBook(addedAt = now) }
    }

    override suspend fun getPageCount(book: ComicBook): Int {
        // Реализуется в reader-слое при открытии файла
        return book.pageCount
    }

    override suspend fun getPageData(book: ComicBook, pageIndex: Int): ByteArray {
        // Реализуется в feature:reader
        throw UnsupportedOperationException("Page rendering is handled by the reader module")
    }

    override suspend fun getCoverData(book: ComicBook): ByteArray? {
        // Реализуется в feature:reader (первая страница как обложка)
        return null
    }
}
