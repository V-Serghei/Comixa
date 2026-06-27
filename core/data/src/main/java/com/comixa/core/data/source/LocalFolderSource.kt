package com.comixa.core.data.source

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.comixa.core.data.scanner.DirectoryScanner
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
    private val directoryScanner: DirectoryScanner,
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

        // Android 11+ with MANAGE_EXTERNAL_STORAGE: direct File API — no SAF restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            return directoryScanner.scan().toComicBookFlow(now)
        }

        // Fallback: MediaStore (fast, indexed) + SAF folder the user picked
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
