package com.comixa.core.data.source

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.comixa.core.data.scanner.FileScanner
import com.comixa.core.data.scanner.MediaStoreScanner
import com.comixa.core.data.scanner.ScannedFile
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.source.ComicSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFolderSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileScanner: FileScanner,
    private val mediaStoreScanner: MediaStoreScanner,
) : ComicSource {

    override val sourceId: String = "local"
    override val displayName: String = "Local Library"

    @Volatile private var rootUri: Uri? = null

    fun setRootUri(uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
        rootUri = uri
    }

    override fun scan(): Flow<ComicBook> {
        val now = System.currentTimeMillis()
        val mediaFlow = mediaStoreScanner.scan().toComicBookFlow(now)
        val safUri = rootUri
        return if (safUri != null) {
            merge(mediaFlow, fileScanner.scan(safUri).toComicBookFlow(now))
        } else {
            mediaFlow
        }
    }

    private fun Flow<ScannedFile>.toComicBookFlow(addedAt: Long): Flow<ComicBook> =
        map { it.toComicBook(addedAt) }

    override suspend fun getPageCount(book: ComicBook): Int = book.pageCount

    override suspend fun getPageData(book: ComicBook, pageIndex: Int): ByteArray =
        throw UnsupportedOperationException("Page rendering is handled by the reader module")

    override suspend fun getCoverData(book: ComicBook): ByteArray? = null
}
