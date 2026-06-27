package com.comixa.core.data.scanner

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MediaStoreScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val detectors: Set<@JvmSuppressWildcards FormatDetector>,
) {

    private val sortedDetectors by lazy {
        detectors.sortedByDescending { it.priority }
    }

    fun scan(): Flow<ScannedFile> = flow {
        val resolver = context.contentResolver
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
        )

        // SQLite LIKE is ASCII-case-insensitive, so '.pdf' matches '.PDF' too
        val selection = buildString {
            append("(")
            append("${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE '%.pdf'")
            append(" OR ${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE '%.cbz'")
            append(" OR ${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE '%.zip'")
            append(" OR ${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE '%.cbr'")
            append(")")
            append(" AND ${MediaStore.Files.FileColumns.SIZE} > 0")
        }

        resolver.query(collection, projection, selection, null, null)?.use { cursor ->
            val idCol   = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id       = cursor.getLong(idCol)
                val name     = cursor.getString(nameCol) ?: continue
                val mimeType = if (mimeCol >= 0) cursor.getString(mimeCol) ?: "" else ""
                val size     = cursor.getLong(sizeCol)

                val detector = sortedDetectors.firstOrNull { it.canHandle(name, mimeType) }
                    ?: continue

                val fileUri = ContentUris.withAppendedId(collection, id)
                emit(ScannedFile(uri = fileUri, fileName = name, format = detector.format(), sizeBytes = size))
            }
        }
    }.flowOn(Dispatchers.IO)
}
