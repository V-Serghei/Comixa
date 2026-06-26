package com.comixa.core.data.scanner

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Сканирует папку, выбранную пользователем через SAF (Storage Access Framework).
 *
 * Использует DocumentsContract напрямую вместо DocumentFile.listFiles(),
 * что даёт 30-50x прирост скорости на больших библиотеках — один
 * ContentResolver.query() на папку вместо одного IPC-вызова на файл.
 *
 * Алгоритм: BFS (обход в ширину) — предсказуемое использование памяти,
 * результаты эмитятся по мере нахождения через Flow.
 */
class FileScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val detectors: Set<@JvmSuppressWildcards FormatDetector>,
) {

    private val sortedDetectors by lazy {
        detectors.sortedByDescending { it.priority }
    }

    /**
     * Запускает сканирование начиная с rootUri (результат ACTION_OPEN_DOCUMENT_TREE).
     * Эмитирует ScannedFile по мере нахождения — UI может обновляться прогрессивно.
     */
    fun scan(rootUri: Uri): Flow<ScannedFile> = flow {
        val resolver = context.contentResolver
        val rootDocId = DocumentsContract.getTreeDocumentId(rootUri)

        // BFS очередь — содержит document IDs папок для обхода
        val queue = ArrayDeque<String>()
        queue.add(rootDocId)

        while (queue.isNotEmpty()) {
            val parentDocId = queue.removeFirst()
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                rootUri,
                parentDocId,
            )

            resolver.query(
                childrenUri,
                PROJECTION,
                null,
                null,
                null,
            )?.use { cursor ->
                val idIndex   = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)

                while (cursor.moveToNext()) {
                    val docId    = cursor.getString(idIndex)
                    val name     = cursor.getString(nameIndex) ?: continue
                    val mimeType = cursor.getString(mimeIndex) ?: ""
                    val size     = cursor.getLong(sizeIndex)

                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        queue.add(docId)
                    } else {
                        val detector = sortedDetectors.firstOrNull { it.canHandle(name, mimeType) }
                        if (detector != null) {
                            val fileUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId)
                            emit(
                                ScannedFile(
                                    uri = fileUri,
                                    fileName = name,
                                    format = detector.format(),
                                    sizeBytes = size,
                                )
                            )
                        }
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private val PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
        )
    }
}
